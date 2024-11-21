package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityRoleProperties {

	EMPLOYEE ("ROLE_EMPLOYEE"),
	 REPORTING_MANAGER ("ROLE_REPORTING_MANAGER"),
	  MANAGER ( "ROLE_MANAGER"),
	   ADMIN ("ROLE_ADMIN"),
	  LOGIN_SUCCESS ("success"),
	 FROM_CONTENT  ("Altrocks tech EMS 2.0"),
	 MFA_SUCCESS_MSG ("Mail sent successfully..!"),
	 MAIL_SUBJECT  ("Multi factor Authentication - One Time Password ");
	
	private String message;
}
