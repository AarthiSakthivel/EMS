package com.ems2p0.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ems2p0.dto.request.RequestPermissionDto;

import jakarta.mail.internet.ParseException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PermissionRequestValidator {

    /**
     * Method to validate the requested or created permission's date
     * <p>
     * * @author Aarthi Shakthivel
     *
     * @param permissionDto
     * @throws java.text.ParseException
     * @throws ParseException
     */
    public static void request(RequestPermissionDto permissionDto) throws ParseException, java.text.ParseException {

    	PermissionRequestValidator.validateMonthAndDate(permissionDto.date().toString(), permissionDto.month());

        if (permissionDto.date().isBefore(LocalDate.now())) {
            log.error("Requested permission date is invalid"); 
            throw new IllegalArgumentException("Requested permission date is invalid..!!");
        }

        if (ObjectUtils.notEqual(permissionDto.date().getYear(), LocalDate.now().getYear())) {
            log.error("Requested permission year is invalid");
            throw new IllegalArgumentException("Requested permission year is invalid..!!");
        }

        LocalDate plusDays = LocalDate.now().plusDays(7);
        if (permissionDto.date().equals(plusDays) && !permissionDto.startTime().isBefore(LocalTime.now())) { 
                log.error("Requested permission time is invalid : {}", permissionDto.date());
                throw new IllegalArgumentException("Requested permission time is invalid..!!");
            }
        

        if (permissionDto.date().equals(LocalDate.now())
                || (permissionDto.date().isAfter(LocalDate.now()) && permissionDto.date().isBefore(plusDays))) {
            if (permissionDto.date().getDayOfWeek() != DayOfWeek.SATURDAY
                    && permissionDto.date().getDayOfWeek() != DayOfWeek.SUNDAY) {
                log.info("Requested permission date is valid ");
            } else {
                log.error("Invalid Permission Date - requested for Weekends : {}", permissionDto.date());
                throw new IllegalArgumentException("Invalid Permission Date - requested for Weekends..!!");
            }
        } else {
            log.error("Requested permission date is invalid : {}", permissionDto.date());
            throw new IllegalArgumentException("Requested permission date is invalid..!!");
        }

        PermissionRequestValidator.validatePermissionTime(permissionDto);
    }

    /**
     * Method to validate the permission time requested by the employee
     * <p>
     * * @author Aarthi Shakthivel
     *
     * @param permissionDto
     */
    public static void validatePermissionTime(RequestPermissionDto permissionDto) {


        LocalTime startTimeLimit = LocalTime.of(9, 0);
        LocalTime endTimeLimit = LocalTime.of(18, 0);

		if (!permissionDto.startTime().isBefore(permissionDto.endTime())) { 
			throw new IllegalArgumentException("Invalid PermissionTime");
		}


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime currentTime = LocalTime.parse(LocalTime.now().format(formatter), formatter);

        LocalTime startTime = LocalTime.parse(permissionDto.startTime().toString(), formatter);

        if (startTime.equals(currentTime)) {
        } else {
        	if (permissionDto.date().isEqual(LocalDate.now()) && permissionDto.startTime().isBefore(LocalTime.now())) {
                    log.error("Requested permission time is invalid : {}", permissionDto.startTime());
                    throw new IllegalArgumentException("Requested permission time is invalid..!!");
                }
            }
        


        if (!permissionDto.startTime().isBefore(permissionDto.endTime())) {
            throw new IllegalArgumentException("Invalid PermissionTime"); 
        }

        if (Duration.between(permissionDto.startTime(), permissionDto.endTime()).getSeconds() < 600) {
            log.error("Requested permission time is should be more than 10 minutes : {}", permissionDto.startTime());
            throw new IllegalArgumentException("Requested permission time is should be more than 10 minutes..!!");
        }

        if (!ObjectUtils.notEqual(permissionDto.startTime(), permissionDto.endTime())) {
            throw new IllegalArgumentException("Invalid PermissionTime");
        } else if (!permissionDto.startTime().isBefore(startTimeLimit)
                && !permissionDto.endTime().isAfter(endTimeLimit)) {
            log.info("Requested permission time is valid");
        } else {
            log.error("Requested permission time is invalid");
            throw new IllegalArgumentException("Input time should be in between the time of 9.00 A.M to 6.00 P.M");
        }
    }

    /**
     * Validate and return the permission month with requested permission month
     *
     * @param
     * @return
     */
    public static void validateMonthAndDate(String permissionDate, String relevantMonth)
            throws ParseException, java.text.ParseException {
        try {
            DateFormat permissionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date requestedDate = permissionDateFormat.parse(permissionDate);

//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(requestedDate);

            // Extract month and year from the parsed date correctly in "MMMM-yyyy" format
            String requestedMonthYear = new SimpleDateFormat(Ems2p0Constants.EMS_MONTH_FORMAT).format(requestedDate);
              
            if (!requestedMonthYear.equalsIgnoreCase(relevantMonth)) { 
                throw new IllegalArgumentException("Permission month is invalid"); 
            }
        } catch (Exception e) {
            log.error("Exception occured while validating date and month");
            throw new IllegalArgumentException("Permission month is Invalid");
        }
    }
}