package com.ems2p0.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestPermissionDto(Integer id,
//                                   @NotNull(message = "Employee Id is mandatory")
//                                   Integer empId,
//
//                                   @Size(min = 5, max = 50, message = "Username should be min 5 char & max 50 char")
//                                   @NotBlank(message = "Username shouldn't be empty")
//                                   String userName,

                                   @NotBlank(message = "Month shouldn't be empty")
                                   String month,

                                   @JsonFormat(pattern = "dd-MM-yyyy")
                                   LocalDate date,

                                   @JsonFormat(pattern = "HH:mm") LocalTime startTime,

                                   @JsonFormat(pattern = "HH:mm") LocalTime endTime,

                                   String reason) {
}
