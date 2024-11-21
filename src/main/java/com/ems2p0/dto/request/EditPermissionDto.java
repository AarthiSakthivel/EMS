package com.ems2p0.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ems2p0.enums.Ems2p0Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Accessors(chain = true)
public class EditPermissionDto {

	@NotNull(message = "Id is mandatory")
	Long id;

	@JsonFormat(pattern = "dd-MM-yyyy")
	LocalDate date;

	@NotBlank(message = "Month shouldn't be empty")
	String month;

	@JsonFormat(pattern = "HH:mm")
	LocalTime startTime;

	@JsonFormat(pattern = "HH:mm")
	LocalTime endTime;

	@NotBlank(message = "Reason is mandatory")
	String reason;

	Ems2p0Status status;

}
