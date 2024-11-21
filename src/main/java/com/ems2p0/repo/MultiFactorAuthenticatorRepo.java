package com.ems2p0.repo;

import com.ems2p0.enums.OtpStatus;
import com.ems2p0.model.MultiFactorAuthentication;
import com.ems2p0.model.UserDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface MultiFactorAuthenticatorRepo extends JpaRepository<MultiFactorAuthentication, Integer> {
    Optional<MultiFactorAuthentication> findByOtpAndOtpStatus(Integer otp, OtpStatus otpStatus);

    MultiFactorAuthentication findByUserDetails(UserDetails userDetails);

	Optional<MultiFactorAuthentication> findByOtpAndOtpStatusAndUserDetails(Integer otp, OtpStatus active, UserDetails userDetails);
}
