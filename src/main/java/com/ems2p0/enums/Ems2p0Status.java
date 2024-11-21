package com.ems2p0.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Ems2p0Status {

    APPROVED("Approved"),//0
    REJECTED("Rejected"),//1
    PENDING("Pending"),//2
    CANCELLED("Cancelled");//3

    private final String value;
}
