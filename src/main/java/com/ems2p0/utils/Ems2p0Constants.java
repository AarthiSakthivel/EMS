package com.ems2p0.utils;

/**
// * EMS 2.0 - Constants layer to provide the immutable values and response to
// * maintain re-usability as well as efficient code usage
// *
// * @author Mohan
// * @category Constants layer
// * @apiNote - Developer should be responsible to add the generic values or
// *          constants to declare or invoke anywhere by using this layer to
// *          minimize and avoid the usage of hard coded values and this leads to
// *          best practice.
// */ 
public interface Ems2p0Constants {

	// Generic properties 
	public static final String SUCCESS = "Success";
	public static final String COLON_SPLITTER = ":";
	public static final String SPACE_SPLITTER = " ";
	public static final String FAILURE = "Failed";

	// Security role properties
	public static final String EMPLOYEE = "ROLE_EMPLOYEE";
	public static final String REPORTING_MANAGER = "ROLE_REPORTING_MANAGER";
	public static final String MANAGER = "ROLE_MANAGER";
	public static final String ADMIN = "ROLE_ADMIN";
	public static final String LOGIN_SUCCESS = "success";
	public static final String FROM_CONTENT = " EMS 2.0";
	public static final String MFA_SUCCESS_MSG = "Mail sent successfully..!";
	public static final String MAIL_SUBJECT = "Multi factor Authentication - One Time Password ";

	// Work type properties
	public static final String WORK_TYPE_ONE = "1";
	public static final String WORK_TYPE_TWO = "2";
	public static final String WORK_TYPE_THREE = "3";

	// Utility properties
	public static final String NTP_SERVER = "pool.ntp.org";
	public static final String UTC = "UTC";
	public static final String ZONE_ID = "Asia/Kolkata";
	public static final CharSequence START_IN_TIME = "09:00:00";
	public static final CharSequence END_IN_TIME = "18:00:00";
	public static final String EMS_MONTH_FORMAT = "MMMM-yyyy";
	public static final String EMP = "EMP";
	public static final String EMPID_FORMAT = "%04d";
}
