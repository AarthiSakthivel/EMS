package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionErrorMsg {
	
	INVALID_PERMISSION_TIME("Invalid Permission Time");
	
	private String message;
}
