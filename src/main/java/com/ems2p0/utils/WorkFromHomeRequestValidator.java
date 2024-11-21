package com.ems2p0.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.enums.WorkFromHomeSessionMsg;

import jakarta.mail.internet.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
public class WorkFromHomeRequestValidator {
	
//	private final Ems2p0Utility utility;
    
	public static void validateDateAndYear(WfhRequestDto wfhRequestDto) throws ParseException, java.text.ParseException{
		 
			//String month = wfhRequestDto.month();
		

		Set<List<LocalDate>> finalstartDate = WorkFromHomeRequestValidator.getWeekend(wfhRequestDto).keySet();

		LocalDate startDate = finalstartDate.stream().toList().get(0).get(0);

		Set<List<LocalDate>> finalendDate = WorkFromHomeRequestValidator.getWeekend(wfhRequestDto).keySet();

		LocalDate endDate = finalendDate.stream().toList().get(0).get(1);
		
	//	WorkFromHomeRequestValidator.validateMonthAndDate(startDate.toString(), endDate.toString(), wfhRequestDto.month());

		    
			LocalDate currentDate = LocalDate.now();
			 if(startDate.isBefore(LocalDate.now())) {
				 log.info("Requested Work From Home StartDate is Invalid : {}" +startDate);
				 throw new IllegalArgumentException("Requested Date is invalid"); 
		        }
			 
//			 if(endDate.isEqual(startDate) || endDate.isAfter(startDate)) {
//				 log.info("Requested Work From Home EndDate is valid");
//			 }
//			 else {
//				 log.info("Requested Work From Home EndDate is invalid : {}" + wfhRequestDto.endDate());
//				 throw new IllegalArgumentException("Requested EndDate is invalid"); 
//			 }
			 	 
			 if (!(startDate.isEqual(endDate)) && wfhRequestDto.requestedSession().getValue()
						.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) { 
					log.info("Requested work from home Date is invalid : {}" + startDate, endDate);
					throw new IllegalArgumentException("Requested work from home StartDate and EndDate should be same when requested session will be HALF_DAY");
			 }
			 
		   
		  
		    
	
	}
	   public static void validateMonthAndDate(String wfhStartDate, String wfhEndDate, String relevantMonth)
	            throws ParseException, java.text.ParseException {
	        try {
	            DateFormat wfhDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	            Date requestedStartDate = wfhDateFormat.parse(wfhStartDate);
	            Date requestedEndDate = wfhDateFormat.parse(wfhEndDate);
	            
	            // Extract month and year from the parsed date correctly in "MMMM-yyyy" format
	            String requestedStartDateMonthYear = new SimpleDateFormat(Ems2p0Constants.EMS_MONTH_FORMAT).format(requestedStartDate);
	            String requestedEndDateMonthYear = new SimpleDateFormat(Ems2p0Constants.EMS_MONTH_FORMAT).format(requestedEndDate);

	              
	            if (!requestedStartDateMonthYear.equalsIgnoreCase(relevantMonth)) { 
	                throw new IllegalArgumentException("Permission StartDate month is invalid"); 
	            }
	            
	            if (!requestedEndDateMonthYear.equalsIgnoreCase(relevantMonth)) { 
	                throw new IllegalArgumentException("Permission EndDate month is invalid"); 
	            }
	        } catch (Exception e) {
	            log.error("Exception occured while validating date and month");
	            throw new IllegalArgumentException("Requested Work From month month is Invalid");
	        }
	    }
	   
	  public static Map<List<LocalDate>, Map<LocalDate, Boolean>> getWeekend (WfhRequestDto wfhRequestDto)  {
		  	       LocalDate startDate1 = wfhRequestDto.startDate();
	       
		   LocalDate endDate1 = wfhRequestDto.endDate(); 
		   
		   LocalDate currentDate = LocalDate.now();
		   	
		  
		  if (startDate1.getDayOfWeek() == DayOfWeek.SATURDAY
                  || startDate1.getDayOfWeek() == DayOfWeek.SUNDAY) {
			 log.error("Invalid StartDate - requested for Weekends : {}",startDate1);
              throw new IllegalArgumentException("Invalid request Date - requested for Weekends..!!");
          } 		 

		 if (endDate1.getDayOfWeek() == DayOfWeek.SATURDAY
                  || endDate1.getDayOfWeek() == DayOfWeek.SUNDAY) {
			 log.error("Invalid Enddate - requested for Weekends : {}", endDate1);
              throw new IllegalArgumentException("Invalid request Date - requested for Weekends..!!"); 
          } 
		 
			Month requiredMonth = currentDate.getMonth().plus(1);
			
			Month requiredNextMonth = currentDate.getMonth().plus(2);
		 
//			if (!(startDate1.getMonth() == currentDate.getMonth()|| (startDate1.getMonth()== requiredMonth))) {
//				log.info("Requested Work From Home StartDate month is inValid : {}" +wfhRequestDto.startDate() );
//				throw new IllegalArgumentException("Requested StartDate month is invalid");
//		   	}
//		  
//		 if(!(endDate1.getMonth()== currentDate.getMonth() || (endDate1.getMonth()== requiredMonth))){ 
//				log.info("Requested Work From Home EndDate month is inValid : {}" +wfhRequestDto.endDate());
//				 throw new IllegalArgumentException("Requested EndDate month is invalid"); 
//			}

		 
		 if(startDate1.getMonth().getValue()==currentDate.getMonth().getValue() || startDate1.getMonth().getValue()== requiredMonth.getValue()) {
			 
			 log.info("Requested Work From Home StartDate month is Valid : {}" + startDate1.getMonth()); 
		 }
		 else if(startDate1.getMonth().getValue()==currentDate.getMonth().getValue() || startDate1.getMonth().getValue()== requiredNextMonth.getValue()) {
			 log.info("Requested Work From Home StartDate month is Valid : {}" + startDate1.getMonth());
		 }
		 else {
			 log.info("Requested Work From Home StartDate month is inValid : {}" +wfhRequestDto.startDate() );
				throw new IllegalArgumentException("Requested StartDate month is invalid");
		 }
		 
       if(endDate1.getMonth().getValue()==currentDate.getMonth().getValue() || endDate1.getMonth().getValue()== requiredMonth.getValue()) {
			 
			 log.info("Requested Work From Home EndDate month is Valid : {}" + endDate1.getMonth().getValue());
		 }
       
       else if ((endDate1.getMonth().getValue()==currentDate.getMonth().getValue() || endDate1.getMonth().getValue()== requiredNextMonth.getValue())) {
    	   
    	   log.info("Requested Work From Home EndDate month is Valid : {}" + endDate1.getMonth().getValue());
       }
		 else {
			 log.info("Requested Work From Home EndDate month is inValid : {}" +endDate1.getMonth());
				throw new IllegalArgumentException("Requested EndDate month is invalid");
		 }
		
       if(endDate1.isEqual(startDate1) || endDate1.isAfter(startDate1)) {
			 log.info("Requested Work From Home EndDate is valid");
		 }
		 else {
			 log.info("Requested Work From Home EndDate is invalid : {}" + wfhRequestDto.endDate());
			 throw new IllegalArgumentException("Requested EndDate is invalid"); 
		 }
    	if (!(startDate1.getYear() == currentDate.getYear())) {
	   		log.info("Requested Work From Home StartDate year is inValid : {}" +wfhRequestDto.startDate() );
			throw new IllegalArgumentException("Requested year is invalid");
	   	}
	    
	    if(!(endDate1.getYear() == currentDate.getYear())){
			log.info("Requested Work From Home EndDate year is inValid : {}" +wfhRequestDto.endDate());
			 throw new IllegalArgumentException("Requested EndDate year is invalid"); 
		}
       
       
	   Map<LocalDate, Boolean> getWeekEndDays = Ems2p0Utility.getWeekendDays(wfhRequestDto); 
		  

	   LocalDate startDate = getWeekEndDays.keySet().stream().map((date) -> date).min(LocalDate::compareTo).get();
		
	   LocalDate endDate = getWeekEndDays.keySet().stream().map((date) -> date).max(LocalDate::compareTo).get();
		
		log.info("Get StartDate: " + startDate); 
		
		log.info("Get End Date: " + endDate);
		
	   return Map.of(List.of(startDate, endDate),getWeekEndDays);
		
	  }
}
