package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenericProperties {
 
	 SUCCESS ("Success"),
	  COLON_SPLITTER ( ":"),
	  SPACE_SPLITTER ( " "),
	 FAILURE ("Failed");

	private String message;
}
