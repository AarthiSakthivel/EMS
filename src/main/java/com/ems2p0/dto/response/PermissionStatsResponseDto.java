package com.ems2p0.dto.response;

import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PermissionStatsResponseDto {

	private Long id;

	private String month;

	private LocalTime totalPermission;

	private LocalTime remainingPermission;

	private LocalTime hoursTaken;

	private LocalTime overduePermission;
}
