package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkFromHomeSessionMsg {

	HALF_DAY("Half_Day"),
	FULL_DAY("Full_Day");
	
	private final String value;
}
