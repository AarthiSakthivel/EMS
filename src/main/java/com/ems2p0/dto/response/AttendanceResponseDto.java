package com.ems2p0.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(content = Include.NON_NULL)
public record AttendanceResponseDto(Integer last_Id, String empId, String username, String worktype, String locationIn,
		LocalDate indate, LocalTime intime, LocalDate outdate, LocalTime outtime) {

}
