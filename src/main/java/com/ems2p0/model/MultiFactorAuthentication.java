package com.ems2p0.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import com.ems2p0.enums.OtpStatus;

@Entity(name = "mfa_auth")
@Data
@Accessors(chain = true)
public class MultiFactorAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private Integer otp;

    private OtpStatus otpStatus;

    private LocalDateTime createdDateTime;

    private LocalDateTime modifiedDateTime;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "userCredentialEmp_id")
    private UserDetails userDetails;

}
