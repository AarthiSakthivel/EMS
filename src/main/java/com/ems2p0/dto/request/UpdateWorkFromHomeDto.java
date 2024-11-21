package com.ems2p0.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Accessors(chain = true)

public class UpdateWorkFromHomeDto {
	private Integer id;
	private String noOfDays;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate startDate;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate endDate;

	private String reason;

}
