package com.ems2p0.projections;

import java.time.LocalTime;

public interface PermissionStatsProjection {

    public LocalTime getOverDuePermission();

    public LocalTime gethoursTaken();
}
