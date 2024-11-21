package com.ems2p0.serviceImpl;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ems2p0.components.EmailComponent;
import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.request.EmailIdRequestDto;
import com.ems2p0.dto.request.LoginRequestDto;
import com.ems2p0.dto.request.ValidateAuthDto;
import com.ems2p0.dto.response.ForgotPwdResponseDto;
import com.ems2p0.dto.response.LoginResponseDto;
import com.ems2p0.dto.response.MFAResponseDto;
import com.ems2p0.dto.response.RefreshTokenResponseDto;
import com.ems2p0.mapper.attendance.AttendanceMapper;
import com.ems2p0.model.MultiFactorAuthentication;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.security.JwtUtils;
import com.ems2p0.security.multi_factor.MultifactorAuthenticator;
import com.ems2p0.service.AuthenticationService;
import com.ems2p0.utils.Ems2p0Constants;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 - Service implementation layer which is responsible to do all api
 * operations and business logics.
 *
 * @author Mohan
 * @category Authentication module ServiceImpl - Business layer
 * @apiNote - Developer should be responsible to each and every api method will
 *          be simple to read and write and should it should be co ordinating
 *          with utility methods to reuse the logics by maintaining the high
 *          level code quality by reduce the boiler plates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

	/**
	 * Injected security user service to do the DB operations
	 */
	private final EmsDaoService daoService;

	/**
	 * Injected Multi factor authenticator invoke the OTP methods and logics
	 */
	private final MultifactorAuthenticator multifactorAuthenticator;


	/**
	 * Injected JWT utils to generate, parse and validate the token
	 */
	private final JwtUtils jwtUtils;

	/**
	 * Injected Email component or bean to trigger the javax mail service
	 */
	private final EmailComponent emailComponent;

	/**
	 * Injected attendance mapper to invoke the mapper methods for dto to entity
	 * conversion
	 */
	private final AttendanceMapper attendanceMapper;

	@Value("${app.jwt.refresh.expirationMs}")
	private Long refreshTokenExpiration;

	@Value("${app.jwt.access.expirationMs}")
	private Long accessTokenExpiration;

	/**
	 * Method to return welcome page response
	 */
	@Override
	public String welcomePage() {
		return "Welcome to EMS2p0 App";
	}

	@Override
	public MFAResponseDto authenticateByMultiFactor(EmailIdRequestDto emailIdRequestDto)
			throws MessagingException, UnsupportedEncodingException {
		EmployeeProjection employee = daoService.loadEmployeeByEmailId(emailIdRequestDto.emailId());
		UserDetails userDetails = daoService.loadUserByUsername(employee.getUserName());
		MultiFactorAuthentication authentication = multifactorAuthenticator.persistMultiFactorAuthDetails(userDetails);
		userDetails.setMultiFactorAuthentication(authentication);
		daoService.save(userDetails);
		emailComponent.sendEmail(employee.getEmailId(), employee.getEmpName() ,authentication.getOtp());
		return new MFAResponseDto(userDetails.getUsername(), Ems2p0Constants.MFA_SUCCESS_MSG);
	}

	@Override
	public LoginResponseDto validateMultiFactorAuthentication(ValidateAuthDto validateAuthDto) {
		UserDetails userDetails = daoService.loadUserByUsername(validateAuthDto.userName());
		String role = ObjectUtils.isNotEmpty(userDetails.getEmployeeRoleManagement())
				&& ObjectUtils.isNotEmpty(userDetails.getEmployeeRoleManagement().getOfficialRole())
						? userDetails.getEmployeeRoleManagement().getOfficialRole().name()
						: "";
		MultiFactorAuthentication multiFactorAuthentication = multifactorAuthenticator
				.validateAuthenticationDetails(validateAuthDto.otpValue(), userDetails);
		if (ObjectUtils.notEqual(validateAuthDto.otpValue(), multiFactorAuthentication.getOtp())
		        && ObjectUtils.isNotEmpty(userDetails.getMultiFactorAuthentication())
		        && ObjectUtils.isNotEmpty(userDetails.getMultiFactorAuthentication().getOtp())) {
		
				ObjectUtils.notEqual(userDetails.getMultiFactorAuthentication().getOtp(), validateAuthDto.otpValue());
				throw new CustomExceptionDto("Invalid OTP....!");
			}
		
		String access_token = jwtUtils.generateToken(userDetails, accessTokenExpiration);
		String refresh_token = jwtUtils.generateToken(userDetails, refreshTokenExpiration);
		return new LoginResponseDto(userDetails.getEmpId(), userDetails.getEmpName(), role, access_token,
				refresh_token); 
	}

	@Override
	public RefreshTokenResponseDto requestRefreshToken() {
		try {
			String accessTokenByUsername = multifactorAuthenticator.getLoggedInUserDetail();
			UserDetails userDetails = daoService.loadUserByUsername(accessTokenByUsername);
			String access_token = jwtUtils.generateRefreshToken(userDetails, accessTokenExpiration);
			String refresh_token = jwtUtils.generateRefreshToken(userDetails, refreshTokenExpiration);
			return new RefreshTokenResponseDto(userDetails.getEmpId(), userDetails.getUsername(), access_token,
					refresh_token);
		} catch (Exception e) {
			log.error("Exception occurred while invoking the token methods : {} ", e.toString());
			throw new CustomExceptionDto("Exception occurred while generating token - " + e.getMessage());
		}
	}
//	
//	@Override
//	public void logout() {
//		try {
//			log.info("Entering the method");
//			String userName = multifactorAuthenticator.getLoggedInUserDetail();
//			UserDetails userDetails = daoService.loadUserByUsername(userName);
//			userDetails.setEmpDeviceToken(null);
//			daoService.save(userDetails);
//			log.info("Token cleared and saved successfully");
//		} catch (Exception e) {
//			log.error("Exception occured while clearing the device token : {}", e.toString());
//			throw new CustomExceptionDto("Something went wrong with signout...");
//		}
//	}


	@Override
	public void logout() {
		try {
			log.info("Entering the method");
			String userName = multifactorAuthenticator.getLoggedInUserDetail();
			UserDetails userDetails = daoService.loadUserByUsername(userName);
			userDetails.setEmpDeviceToken(null);
			daoService.save(userDetails);
			log.info("Token cleared and saved successfully");
		} catch (Exception e) {
			log.error("Exception occured while clearing the device token : {}", e.toString());
			throw new CustomExceptionDto("Something went wrong with signout...");
		}
	}

	

}
