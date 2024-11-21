package com.ems2p0.service;

import java.io.UnsupportedEncodingException;

import com.ems2p0.dto.request.EmailIdRequestDto;
import com.ems2p0.dto.request.ValidateAuthDto;
import com.ems2p0.dto.response.LoginResponseDto;
import com.ems2p0.dto.response.MFAResponseDto;
import com.ems2p0.dto.response.RefreshTokenResponseDto;

import jakarta.mail.MessagingException;

/**
 * EMS 2.0 - Interface layer to maintain all api methods and functionalities to
 * hide their business logic and represent the low level visibility to the
 * controller level
 *
 * @author Mohan
 * @category Authentication functionality
 * @Version - v1.0.0
 * @apiNote - Developer should be responsible to declare the abstract method
 *          here and should implement the business logic by the serviceImpl
 *          respectively
 */
public interface AuthenticationService {

    String welcomePage();

    MFAResponseDto authenticateByMultiFactor(EmailIdRequestDto emailIdRequestDto) throws MessagingException, UnsupportedEncodingException;

    LoginResponseDto validateMultiFactorAuthentication(ValidateAuthDto validateAuthDto);

    RefreshTokenResponseDto requestRefreshToken();
    
    void logout();

	

}
