package com.ems2p0.dto.response;

public record RefreshTokenResponseDto(Integer empId, String userName, String accessToken, String refreshToken) {
}
