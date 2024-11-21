package com.ems2p0.projections;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ems2p0.enums.WorkFromHomeSessionMsg;

public interface WfhProjection {
     
	   public Integer getid();

	    public String getmonth();
 
	    public int getRequested_session();
	   
	    public LocalDate getstart_date();

	    public LocalDate getend_date();

	    public Integer getwfh_status();

	    public String getwfh_reason();

	    public Integer getEmpId();

	    public String getuserName();
	    
	    public String getno_of_days();
	    
	    public Boolean getisOverDue(); 

	    
}
