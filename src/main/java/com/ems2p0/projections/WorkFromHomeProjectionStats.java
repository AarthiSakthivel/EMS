package com.ems2p0.projections;

import java.time.LocalTime;

public interface WorkFromHomeProjectionStats {

	public double getOverDueWfhDays();

	public double getRemainingWfhDays();
	
	public double getDaysTaken();
}
