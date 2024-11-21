package com.ems2p0.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.WorkFromHomeSessionMsg;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor

public class WfhDetailsResponseDto {
    
	private Long id;

	private String month;

	private LocalDate startDate;

	private LocalDate endDate;
	
	private double noOfDays;

	private WorkFromHomeSessionMsg requestedSession; 

	private Ems2p0Status status;

	private String empId;

	private String userName;

	private String reason;

	private double daysTaken;

	private Boolean isOverDue;

}
