package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Worktype {

	WFO("Work from office"), WFH("Work from home"), ON_SITE("On site");

	private final String value;
}
