package com.ems2p0.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class WfhStatsResponseDto {
  
	private Long id;

	private String month;

	private Integer totalWfhDays;

	private double remainingWfhDays;

	private double daysTaken;

	private double overdueWfhDays;

}
