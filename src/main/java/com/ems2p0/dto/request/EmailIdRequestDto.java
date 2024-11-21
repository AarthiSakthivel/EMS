package com.ems2p0.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailIdRequestDto(
        @Email(message = "Enter a valid Email Id") @NotBlank(message = "Email Id shouldn't be empty") String emailId) {
}
