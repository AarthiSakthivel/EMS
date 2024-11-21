package com.ems2p0.security.multi_factor;

import java.time.LocalDateTime;
import java.util.Random;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.enums.OtpStatus;
import com.ems2p0.model.MultiFactorAuthentication;
import com.ems2p0.model.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 -Multi factor authenticator component which is responsible to
 * maintain the authentication methods and logic.
 *
 * @author Mohan
 * @version v1.0.0
 * @category Security module
 * @apiNote - Developer should be responsible to maintain all MFA related logics
 *          and methods within this components and enhance the best practice to
 *          invoke everywhere required in the application.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class MultifactorAuthenticator {

	/**
	 * Injected EMS Data access service
	 */
	private final EmsDaoService daoService;

	/**
	 * Method to generate the OTP for authentication
	 *
	 * @return
	 */
	private Integer generateOtp() {
	Random random = new Random();
		return random.nextInt(9000) + 1000;
	}

	/**
	 * Method to persist or save the multi factor authentication details
	 *
	 * @return
	 */
	public MultiFactorAuthentication persistMultiFactorAuthDetails(UserDetails userDetails) {
		MultiFactorAuthentication existsOtp = daoService.fetchOtpByUserDetailsAndStatus(userDetails);
		if (ObjectUtils.isNotEmpty(existsOtp)) {
			MultiFactorAuthentication authentication = new MultiFactorAuthentication().setId(existsOtp.getId())
					.setOtp(generateOtp()).setOtpStatus(OtpStatus.ACTIVE).setCreatedDateTime(LocalDateTime.now())
					.setModifiedDateTime(LocalDateTime.now()).setUserDetails(userDetails);
			return daoService.persistOtp(authentication);
		} else {
			MultiFactorAuthentication authentication = new MultiFactorAuthentication().setOtp(generateOtp())
					.setOtpStatus(OtpStatus.ACTIVE).setCreatedDateTime(LocalDateTime.now())
					.setModifiedDateTime(LocalDateTime.now()).setUserDetails(userDetails);
			return daoService.persistOtp(authentication);
		}

	}

	/**
	 * Method to validate the OTP in authentication
	 *
	 * @param otp
	 * @return
	 */
	public MultiFactorAuthentication validateAuthenticationDetails(Integer otp, UserDetails userDetails) {
		try {
			MultiFactorAuthentication authentication = daoService.fetchOtp(otp, userDetails)
					.setOtpStatus(OtpStatus.IN_ACTIVE).setModifiedDateTime(LocalDateTime.now());
			daoService.persistOtp(authentication);
			log.info("OTP authenticated successfully.....");
			return authentication;
		} catch (Exception e) {
			log.error("Exception occured while doing OTP validation : {}", e.toString());
			throw new CustomExceptionDto(e.getMessage());
		}
	}

	/**
	 * Method to fetch the details of current loggedIn user
	 *
	 * @return
	 */
	public String getLoggedInUserDetail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = ObjectUtils.isNotEmpty(authentication) && ObjectUtils.isNotEmpty(authentication.getName())
				? authentication.getName()
				: null;
		return StringUtils.isNotEmpty(username) ? username : "";
	}
}
