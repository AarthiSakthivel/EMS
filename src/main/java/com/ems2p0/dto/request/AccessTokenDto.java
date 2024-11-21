package com.ems2p0.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AccessTokenDto(@NotBlank(message = "Access token required") String accessToken) {
}
