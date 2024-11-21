package com.ems2p0.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(value = Include.NON_NULL)
public record AttendanceDto(

//		@NotNull(message = "Employee Id is mandatory")
		Integer empId,

//		 @Size(min = 5, max = 50, message = "Username should be min 5 char & max 50 char")
//         @NotBlank(message = "Username shouldn't be empty")
		String username,

		@NotBlank(message = "Work type should be mandatory") String work_type, 

		@NotBlank(message = "Location should be mandatory") String locationIn,

		@NotNull(message = "Last Id is mandatory") Integer last_Id) {

}
