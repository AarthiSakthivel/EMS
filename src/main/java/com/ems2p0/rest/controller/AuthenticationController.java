package com.ems2p0.rest.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems2p0.dto.request.EmailIdRequestDto;
import com.ems2p0.dto.request.ValidateAuthDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.LoginResponseDto;
import com.ems2p0.dto.response.MFAResponseDto;
import com.ems2p0.dto.response.RefreshTokenResponseDto;
import com.ems2p0.service.AuthenticationService;
import com.ems2p0.utils.Ems2p0Constants;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 -  Enhanced attendance module and employee management
 * system application. All type of high level functionality's API creation and
 * manipulation are handled in this controller
 *
 * @author Mohan
 * @version v.0.0.1
 * @category Gateway - API Controller
 */
@RestController
@RequestMapping("/authentication")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

	/**
	 * Injected ems service interface layer to invoke the api methods
	 */
	private final AuthenticationService ems2p0Service;
	
	

	/**
	 * Api method to for checking the health of the application by returning a
	 * welcome message
	 *
	 * @return {@link - GenericResponseDto<String>}
	 */
	@GetMapping("/welcome-page")
	public ResponseEntity<GenericResponseDto<String>> welcomePage() {
		log.info("Welcome page api invoked....");
		return ResponseEntity.status(HttpStatus.OK)
				.body(new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, ems2p0Service.welcomePage()));
	}



	/**
	 * Api to authenticate the user by multi factor method using emailId
	 *
	 * @param emailIdRequestDto
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/multi-factor")
	public ResponseEntity<GenericResponseDto<MFAResponseDto>> authenticateByMultiFactor(
			@RequestBody @Valid EmailIdRequestDto emailIdRequestDto)
			throws MessagingException, UnsupportedEncodingException {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<MFAResponseDto>(true,
				Ems2p0Constants.SUCCESS, ems2p0Service.authenticateByMultiFactor(emailIdRequestDto)));
	}

	/**
	 * Api to validate the OTP by user
	 *
	 * @param validateAuthDto
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/multi-factor/validate")
	public ResponseEntity<GenericResponseDto<LoginResponseDto>> validateMultiFactorAuthentication(
			@RequestBody @Valid ValidateAuthDto validateAuthDto)
			throws MessagingException, UnsupportedEncodingException {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<LoginResponseDto>(true,
				Ems2p0Constants.SUCCESS, ems2p0Service.validateMultiFactorAuthentication(validateAuthDto))); 
	}

	/**
	 * Api to generate the refresh and access token based on token
	 *
	 * @param accessTokenDto
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/multi-factor/refresh-token")
	public ResponseEntity<GenericResponseDto<RefreshTokenResponseDto>> requestRefreshToken()
			throws MessagingException, UnsupportedEncodingException {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<RefreshTokenResponseDto>(true,
				Ems2p0Constants.SUCCESS, ems2p0Service.requestRefreshToken()));
	}
	
//	@DeleteMapping("/logout")
//	public ResponseEntity<GenericResponseDto<?>> logout() throws MessagingException, UnsupportedEncodingException {
//		ems2p0Service.logout();
//		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, null));
//	}

	@DeleteMapping("/logout")
	public ResponseEntity<GenericResponseDto<?>> logout() throws MessagingException, UnsupportedEncodingException {
		ems2p0Service.logout();
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, null));
	}

}
