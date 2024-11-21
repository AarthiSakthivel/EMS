package com.ems2p0.dto.response;

import java.time.LocalTime;

import com.ems2p0.enums.Ems2p0Status;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PermissionDetailsResponseDto {

	private Long id;

	private String month;

	private String date;

	private String startTime;

	private String endTime;

	private Ems2p0Status status;

	private String empId;

	private String userName;

	private String reason;

	private LocalTime hoursTaken;

	private Boolean isOverDue;
}
