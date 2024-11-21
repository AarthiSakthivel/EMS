package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMsg {
	
	EMPLOYEE_NOT_FOUND("Employee not found...!"),
	PERMISSION_NOT_FOUND("Permission not found...!");
	
	private String message;

}
