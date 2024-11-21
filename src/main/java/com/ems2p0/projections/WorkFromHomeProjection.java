package com.ems2p0.projections;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface WorkFromHomeProjection {

	public Integer getId();
	
    public String getMonth();

    public LocalDate getStartDate();

    public LocalDate getEndDate();
    
    public String getWfhStatus();
    
    public String getWfhReason();
    
    public String getempSerialNo();

    public String getuserName();
}
