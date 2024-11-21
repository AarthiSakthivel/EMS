package com.ems2p0.dto.request;

import java.time.LocalDate;

import com.ems2p0.enums.WorkFromHomeSessionMsg;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;

public record WfhRequestDto(

		Integer id,

		@NotBlank(message = "Month shouldn't be empty") String month,

		@JsonFormat(pattern = "dd-MM-yyyy") LocalDate startDate,

		@JsonFormat(pattern = "dd-MM-yyyy") LocalDate endDate,

		WorkFromHomeSessionMsg requestedSession,

		String reason) { 

}
