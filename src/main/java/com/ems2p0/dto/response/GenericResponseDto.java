package com.ems2p0.dto.response;

public record GenericResponseDto<T>(Boolean status, String message, T content) {
}
