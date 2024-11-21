package com.ems2p0.dto.response;

public record LoginResponseDto(Integer empId, String name, String role, String access_token, String refresh_token) {

}
