package com.ems2p0.projections;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AttendanceProjection {
	
          public Integer getLastId();
          
          public Integer getempId();
          
          public String getusername();
          
          public String getworktype();
          
          public String getlocationIn();
          
          public LocalDate getindate();
          
          public LocalTime intime();
}
