package com.ems2p0.utils;

import java.net.InetAddress;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.exception.PermissionExpiredException;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.WorkFromHomeSessionMsg;
import com.ems2p0.model.EmployeePermissionStats;
import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.EmployeeWfhStats;
import com.ems2p0.repo.EmployeeWfhDetailsRepo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 - Utility component layer to create and maintain the generic methods.
 *
 * @author Mohan
 * @category Utility layer
 * @apiNote Developer should be responsible to maintain all of the generic
 *          methods to increase the re-usability by avoiding the boiler plates.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Ems2p0Utility {

	/**
	 * Method to decode the value by using base64 component module
	 * <p>
	 * * @author Aarthi Shakthivel
	 *
	 * @param value
	 * @return
	 */

	private final EmployeeWfhDetailsRepo employeeWfhDetailsRepo;

	@Value("${client.setDefaultTimeout}")
	private int sessionTime;

	public String decodedValue(String value) {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(value);
			return new String(decodedBytes);
		} catch (Exception e) {
			log.error("Exception occurred while decoding the value... : {}", value);
			return "";
		}
	}

	/**
	 * Method to calculate the permission time of the employee
	 * <p>
	 * * @author Mohan && Aarthi Shakthivel
	 *
	 * @param permissionDto
	 * @param employeePermissionStats
	 * @return {@link - LocalTime}
	 */
	public EmployeePermissionStats aggregatePermissionTime(RequestPermissionDto permissionDto,
			EmployeePermissionStats employeePermissionStats) {

		employeePermissionStats = ObjectUtils.isNotEmpty(employeePermissionStats) ? employeePermissionStats
				: new EmployeePermissionStats();

		LocalTime totalPermissionTime = ObjectUtils.isNotEmpty(employeePermissionStats.getRemainingPermission())
				? employeePermissionStats.getRemainingPermission()
				: LocalTime.of(3, 0);

		LocalTime startTime = permissionDto.startTime();
		LocalTime endTime = permissionDto.endTime();

		long permissionMinutes = ChronoUnit.MINUTES.between(startTime, endTime);

		long hours = permissionMinutes / 60;
		long minutes = permissionMinutes % 60;

		LocalTime remainingPermissionTime = this.aggregateRemainingPermissionTime(permissionDto, totalPermissionTime);

		long overtimeMinutes = permissionMinutes - ChronoUnit.MINUTES.between(LocalTime.MIN, totalPermissionTime);

		LocalTime hoursTaken = ObjectUtils.isNotEmpty(employeePermissionStats)
				&& ObjectUtils.isNotEmpty(employeePermissionStats.getHoursTaken())
						? employeePermissionStats.getHoursTaken().plusMinutes(permissionMinutes)
						: LocalTime.of((int) hours, (int) minutes);

		if (overtimeMinutes <= 0) {
			employeePermissionStats.setHoursTaken(hoursTaken);
			if (hoursTaken.equals(LocalTime.of(3, 0))) {
				remainingPermissionTime = LocalTime.of(0, 0);
			}
			employeePermissionStats.setRemainingPermission(remainingPermissionTime);
			return employeePermissionStats;
		} else {
			long hours2 = overtimeMinutes / 60;
			long minutes2 = overtimeMinutes % 60;
			LocalTime overtimeTime = ObjectUtils.isNotEmpty(employeePermissionStats.getOverduePermission())
					? employeePermissionStats.getOverduePermission().plusMinutes(overtimeMinutes)
					: LocalTime.of((int) hours2, (int) minutes2);
			employeePermissionStats.setHoursTaken(hoursTaken);
			if (hoursTaken.equals(LocalTime.of(3, 0))) {
				remainingPermissionTime = LocalTime.of(0, 0);
			}
			employeePermissionStats.setRemainingPermission(remainingPermissionTime);
			employeePermissionStats.setOverduePermission(overtimeTime);
			return employeePermissionStats;
		}
	}

	/**
	 * Method to aggregate the remaining permission time
	 *
	 * @param permissionDto
	 * @param totalPermissionTime
	 * @return
	 */
	private LocalTime aggregateRemainingPermissionTime(RequestPermissionDto permissionDto,
			LocalTime totalPermissionTime) {

		Duration duration = Duration.between(permissionDto.startTime(), permissionDto.endTime());

		long remainingSeconds = totalPermissionTime.toSecondOfDay() - duration.getSeconds();
		if (remainingSeconds < 0) {
			remainingSeconds = 0;
		}

		long remainingHours = remainingSeconds / 3600;
		long remainingMinutes = (remainingSeconds % 3600) / 60;
		return LocalTime.of((int) remainingHours, (int) remainingMinutes);
	}

	public void validatePermissionUpdate(LocalTime permissionCreatedTime) {
		long minutesDifference = Duration.between(permissionCreatedTime, LocalTime.now()).toMinutes();
		long difference = (minutesDifference < 0) ? Math.abs(minutesDifference) : minutesDifference;
		if (difference >= 10) {
			log.info("Valid permission update operation");
		} else {
			throw new PermissionExpiredException(
					"Permission should be update or cancel within 10 minutes of creation time..");
		}
	}

	public void validateWorkFromHomeUpdate(EmployeeWfhDetails wfhRequestDto) {

		LocalDate startDate = wfhRequestDto.getStartDate();
		LocalTime currentTime = LocalTime.now();
		LocalTime targetTime = LocalTime.of(19, 15);

		if (LocalDate.now().equals(startDate)) {
			if (currentTime.getHour() == targetTime.getHour() && currentTime.getMinute() == targetTime.getMinute()
					|| currentTime.isAfter(targetTime)) {
				throw new PermissionExpiredException(
						"Work from Home request should be update or cancel within 9:00 AM of creation time..");
			} else {
				log.info("Requested valid time to cancel the WFH");
			}
		}

	}

	/**
	 * Method to generate the current date and time by Zone
	 *
	 * @return
	 * @throws Exception
	 */
	public LocalDateTime getOriginalTimeZone() throws Exception {
		NTPUDPClient client = new NTPUDPClient();
		client.setDefaultTimeout(sessionTime);
		try {
			InetAddress inetAddress = InetAddress.getByName(Ems2p0Constants.NTP_SERVER);
			TimeInfo timeInfo = client.getTime(inetAddress);
			long ntpTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
			LocalDateTime originalTimeUTC = Instant.ofEpochMilli(ntpTime).atZone(ZoneId.of(Ems2p0Constants.UTC))
					.toLocalDateTime();
			ZoneId indiaTimeZone = ZoneId.of(Ems2p0Constants.ZONE_ID);
			LocalDateTime originalTimeIndia = originalTimeUTC.atZone(ZoneId.of(Ems2p0Constants.UTC))
					.withZoneSameInstant(indiaTimeZone).toLocalDateTime();
			return originalTimeIndia;
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * Method to convert into minutes format from time
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Long hoursToMinutes(LocalTime startTime, LocalTime endTime) {
		return ChronoUnit.MINUTES.between(startTime, endTime);
	}

	/**
	 * Method to convert the time into minutes
	 *
	 * @param timeString
	 * @return
	 */
	public Integer convertTimeToMinutes(LocalTime timeString) {
		String[] parts = timeString.toString().split(Ems2p0Constants.COLON_SPLITTER);
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		return hours * 60 + minutes;
	}

	/**
	 * Method to find the difference of two different time
	 */
	public long calculateTimeDifference(LocalTime originalStartTime, LocalTime originalEndTime, LocalTime newStartTime,
			LocalTime newEndTime) {
		long originalDuration = originalStartTime.until(originalEndTime, java.time.temporal.ChronoUnit.MINUTES);
		long newDuration = newStartTime.until(newEndTime, java.time.temporal.ChronoUnit.MINUTES);

		return newDuration - originalDuration;
	}

	/**
	 * Method to calculate working hours
	 *
	 * @param intime
	 * @param outtime
	 * @return
	 */
	public double calculateWorkingHours(LocalTime intime, LocalTime outtime) {
		long minutes = ChronoUnit.MINUTES.between(intime, outtime);
		double workingHours = minutes / 60.0;
		return workingHours;
	}

	/**
	 * Method calculate the total working hours
	 *
	 * @param intime
	 * @param outtime
	 * @return
	 */
	public double calculateTotalWorkingHours(LocalTime intime, LocalTime outtime) {
		LocalTime regularIntime = LocalTime.parse(Ems2p0Constants.START_IN_TIME);
		LocalTime regularOuttime = LocalTime.parse(Ems2p0Constants.END_IN_TIME);
		double regularWorkingHours = calculateWorkingHours(regularIntime, regularOuttime);
		double totalWorkingHours = regularWorkingHours + calculateWorkingHours(intime, outtime);
		return totalWorkingHours;
	}

	/**
	 * Method to aggregate work from home date
	 *
	 * @param WfhRequestDto
	 * @param employeeWfhStats
	 * @return
	 */

	public Map<EmployeeWfhStats, Double> aggregateWfhDate(WfhRequestDto wfhRequestDto, EmployeeWfhStats employeeWfhStats) {
		employeeWfhStats = ObjectUtils.isNotEmpty(employeeWfhStats) ? employeeWfhStats : new EmployeeWfhStats();
		double daysTaken = ObjectUtils.allNotNull(employeeWfhStats) ? employeeWfhStats.getDaysTaken() : 0.0;

		// Map<List<LocalDate>, Map<LocalDate, Boolean>>
		Set<List<LocalDate>> startDate = WorkFromHomeRequestValidator.getWeekend(wfhRequestDto).keySet();

		LocalDate finalstartDate = startDate.stream().toList().get(0).get(0);

		Set<List<LocalDate>> endDate = WorkFromHomeRequestValidator.getWeekend(wfhRequestDto).keySet();

		LocalDate finalendDate = endDate.stream().toList().get(0).get(1);

		Set<LocalDate> noOfDaysTaken = WorkFromHomeRequestValidator.getWeekend(wfhRequestDto).values().iterator().next()
				.keySet();

		double dateSize = (double) noOfDaysTaken.size();
		
		if (startDate.equals(endDate) && wfhRequestDto.requestedSession().getValue()
				.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
			dateSize = 0.5;
		}
     
		double noOfDays = dateSize;
		daysTaken += dateSize;
		Integer totalNoOfWfhDays = 4;
		double remainingWfhDays = totalNoOfWfhDays - daysTaken;
		double overDueDaysTaken = Math.max(0, daysTaken - totalNoOfWfhDays);

		employeeWfhStats.setDaysTaken(daysTaken); 
		employeeWfhStats.setRemainingWfhDays(Math.max(0, remainingWfhDays));
		employeeWfhStats.setOverdueWfhDays(overDueDaysTaken);
		employeeWfhStats.setTotalWfhDays(totalNoOfWfhDays);
		
//		 Map<EmployeeWfhStats, Double> resultMap = new HashMap<>();
//		    resultMap.put(employeeWfhStats, noOfDays); 
		    return Map.of(employeeWfhStats, noOfDays);
	}

	public EmployeeWfhStats aggregateWfhDateForEdit(WfhRequestDto wfhRequestDto, EmployeeWfhStats employeeWfhStats) {
		// Ensure employeeWfhStats is not null
		employeeWfhStats = ObjectUtils.isNotEmpty(employeeWfhStats) ? employeeWfhStats : new EmployeeWfhStats();

		// Initialize the days taken to 0.0 if employeeWfhStats is null
		double daysTaken = ObjectUtils.allNotNull(employeeWfhStats) ? employeeWfhStats.getDaysTaken() : 0.0;

		LocalDate startDate = wfhRequestDto.startDate();
		LocalDate endDate = wfhRequestDto.endDate();

		// Calculate the number of weekdays taken including the end date
		double noOfDaysTaken = calculateWeekdaysBetween(startDate, endDate);
		if (startDate.equals(endDate) && wfhRequestDto.requestedSession().getValue()
				.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
			noOfDaysTaken = 0.5;
		}

		System.out.println("no.of.days taken: " + noOfDaysTaken);

		// Retrieve the days taken for the same month, excluding the current request's
		// previous days
		double previousWfhDaysInMonth = getPreviousWfhDaysInMonthExcludingCurrent(wfhRequestDto);

		// Update daysTaken to include previous WFH days taken in the same month
		daysTaken = previousWfhDaysInMonth + noOfDaysTaken;

		// Assuming a fixed total number of WFH days allowed per month
		Integer totalNoOfWfhDays = 4;
		double remainingWfhDays = totalNoOfWfhDays - daysTaken;
		double overDueDaysTaken = Math.max(0, daysTaken - totalNoOfWfhDays);

		// Update employeeWfhStats with the new calculations
		employeeWfhStats.setDaysTaken(daysTaken);
		employeeWfhStats.setRemainingWfhDays(Math.max(0, remainingWfhDays));
		employeeWfhStats.setOverdueWfhDays(overDueDaysTaken);
		employeeWfhStats.setTotalWfhDays(totalNoOfWfhDays);

		return employeeWfhStats;
	}

	// Helper method to calculate the number of weekdays between two dates
	private double calculateWeekdaysBetween(LocalDate startDate, LocalDate endDate) {
		double weekdays = 0;
		LocalDate date = startDate;
		while (!date.isAfter(endDate)) {
			DayOfWeek day = date.getDayOfWeek();
			if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
				weekdays++;
			}
			date = date.plusDays(1);
		}
		return weekdays;
	}

	private double getPreviousWfhDaysInMonth(WfhRequestDto wfhRequestDto, EmployeeWfhStats employeeWfhStats) {
		YearMonth currentMonth = YearMonth.from(wfhRequestDto.startDate());
		LocalDate monthStart = currentMonth.atDay(1);
		LocalDate monthEnd = currentMonth.atEndOfMonth();

		List<EmployeeWfhDetails> wfhRequests = employeeWfhDetailsRepo.findByIdAndDateRange(wfhRequestDto.id(),
				monthStart, monthEnd);

		double totalDays = 0;
		for (EmployeeWfhDetails wfhRequest : wfhRequests) {
			LocalDate startDate = wfhRequest.getStartDate();
			LocalDate endDate = wfhRequest.getEndDate();
			double days = Period.between(startDate, endDate).getDays() + 1;
			if (startDate.equals(endDate) && wfhRequest.getRequestedSession().getValue()
					.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
				days = 0.5;
			}
			totalDays += days;
		}

		return totalDays;
	}
//
//	List<EmployeeWfhDetails> wfhRequests = employeeWfhDetailsRepo.findByIdAndDateRange(wfhRequestDto.id(), monthStart,
//			monthEnd);
//
//	double totalDays = 0;for(
//	EmployeeWfhDetails wfhRequest:wfhRequests)
//	{
//		if (!wfhRequest.getId().equals(wfhRequestDto.id())) {
//			LocalDate startDate = wfhRequest.getStartDate();
//			LocalDate endDate = wfhRequest.getEndDate();
//			double days = calculateWeekdaysBetween(startDate, endDate);
//			if (startDate.equals(endDate) && wfhRequest.getRequestedSession().getValue()
//					.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
//				days = 0.5;
//			}
//			totalDays += days;
//		}
//	}
//
//	return totalDays;
//	}

	/**
	 * Method calculate the Date Difference
	 *
	 * @param originalStartDate
	 * @param originalEndDate
	 * @param requestStartDate
	 * @param requestEndDate
	 * @return
	 */


	
	
	


	/**
	 * Method to aggregate work from home date for edit
	 * 
	 * @param WfhRequestDto
	 * @param employeeWfhStats
	 * @return
	 * @throws Exception
	 */

	// Helper method to calculate the number of weekdays between two dates
//	private double calculateWeekdaysBetween(LocalDate startDate, LocalDate endDate) {
//	    double weekdays = 0;
//	    LocalDate date = startDate;
//	    while (!date.isAfter(endDate)) {
//	        DayOfWeek day = date.getDayOfWeek();
//	        if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
//	            weekdays++; 
//	        }
//	        date = date.plusDays(1);
//	    }
//	    return weekdays;
//	}

	private double getPreviousWfhDaysInMonthExcludingCurrent(WfhRequestDto wfhRequestDto) {
		YearMonth currentMonth = YearMonth.from(wfhRequestDto.startDate());
		LocalDate monthStart = currentMonth.atDay(1);
		LocalDate monthEnd = currentMonth.atEndOfMonth();
		List<EmployeeWfhDetails> wfhRequests = employeeWfhDetailsRepo.findByIdAndDateRange(wfhRequestDto.id(),
				monthStart, monthEnd);
		double totalDays = 0;
		for (EmployeeWfhDetails wfhRequest : wfhRequests) {
			if (!wfhRequest.getId().equals(wfhRequestDto.id())) {
				LocalDate startDate = wfhRequest.getStartDate();
				LocalDate endDate = wfhRequest.getEndDate();
				double days = calculateWeekdaysBetween(startDate, endDate);
				if (startDate.equals(endDate) && wfhRequest.getRequestedSession().getValue()
						.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
					days = 0.5;
				}
				totalDays += days;
			}
		}
		return totalDays;
	}

	public static Map<LocalDate, Boolean> getWeekendDays(WfhRequestDto wfhRequestDto) {
		LocalDate startDate = wfhRequestDto.startDate();
		LocalDate endDate = wfhRequestDto.endDate();
		List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
		Map<LocalDate, Boolean> dateMap = new HashMap<>();
		dates.stream().filter(Objects::nonNull).map(date -> {
			boolean isWeekend = isWeekend(date);
			dateMap.put(date, isWeekend);
			return dateMap;
		}).collect(Collectors.toList());
		dateMap.values().removeIf(date -> date);
		return dateMap;
	}

	public static boolean isWeekend(LocalDate date) {
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
	}

	public EmployeeWfhStats aggregateWfhDateForEdits(WfhRequestDto wfhRequestDto, EmployeeWfhDetails wfhDetails,
			EmployeeWfhStats employeeWfhStats, double differenceInDays) {

		// Same Date and Same Session should throw Error Message
		if (wfhRequestDto.startDate().isEqual(wfhDetails.getStartDate())
				&& wfhRequestDto.endDate().isEqual(wfhDetails.getEndDate())
				&& wfhRequestDto.requestedSession().name().equalsIgnoreCase(wfhDetails.getRequestedSession().name())) {
			throw new CustomExceptionDto(
					"Edit not allowed: The start date, end date, and session type are the same as the existing request.");
		}
		int totalNoOfWfhDays = 4;
		double dateDifference = differenceInDays;
		if (wfhRequestDto.startDate().isEqual(wfhRequestDto.endDate())
				&& WorkFromHomeSessionMsg.HALF_DAY.name().equalsIgnoreCase(wfhRequestDto.requestedSession().name())) {
//			dateDifference = differenceInDays;	
		} else {
//			dateDifference = Double.sum(differenceInDays, 1);
		}
//		if (dateDifference > 0 && wfhRequestDto.requestedSession().name()
//				.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.name())) {
//			throw new CustomExceptionDto("Invalid Requested session....!");
//		}
		double updatedDaysTaken = employeeWfhStats.getDaysTaken();
		log.info("updatedDaysTaken", employeeWfhStats.getDaysTaken());
		double updatedRemainingDays = employeeWfhStats.getRemainingWfhDays();
		log.info("updatedRemainingDays", employeeWfhStats.getRemainingWfhDays());
		double updatedOverdueDays = employeeWfhStats.getOverdueWfhDays();
		log.info("updatedOverdueDays", employeeWfhStats.getOverdueWfhDays());

		// Reducing WFH days
		if (wfhRequestDto.requestedSession().getValue()
				.equalsIgnoreCase(WorkFromHomeSessionMsg.FULL_DAY.getValue())) {
			
		if (dateDifference < 0) {
			double positiveDateDifference = Math.abs(dateDifference);

			if (updatedOverdueDays > 0) {
				updatedOverdueDays -= positiveDateDifference;
				if (updatedOverdueDays < 0) {
					updatedDaysTaken -= positiveDateDifference; // This will be negative, reducing the daysTaken
					updatedOverdueDays = 0;
					updatedRemainingDays = totalNoOfWfhDays - updatedDaysTaken;
				} else {
					updatedDaysTaken -= positiveDateDifference;
					updatedOverdueDays = updatedDaysTaken - totalNoOfWfhDays;
					//updatedOverdueDays = updatedRemainingDays;
				}
			} 
			else {
				updatedDaysTaken -= positiveDateDifference;
				if (updatedDaysTaken < 0) {
					updatedRemainingDays += updatedDaysTaken; // This will be negative, increasing remaining days
					updatedDaysTaken = 0;
					if (updatedRemainingDays < 0) {
						updatedOverdueDays = -updatedRemainingDays;
						updatedRemainingDays = 0;
					}
				} else {
					updatedRemainingDays = totalNoOfWfhDays - updatedDaysTaken;
				}
			}
		}
		// Adding WFH days
		else if (dateDifference > 0) {
			  if(wfhDetails.getRequestedSession().getValue().equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {

//		             originalDuration = calculateWeekdaysBetween(originalStartDate, originalEndDate);
//		             originalDuration+=0.5;
				  dateDifference += 0.5;
		        }
			if (updatedOverdueDays > 0) {
				updatedOverdueDays += dateDifference;
				updatedDaysTaken += dateDifference;
				log.info("updatedOverdueDays after addition: {}", updatedOverdueDays);
			} else {
				updatedDaysTaken += dateDifference;
				log.info("updatedDaysTaken after addition: {}", updatedDaysTaken);

				if (updatedDaysTaken > totalNoOfWfhDays) {
					updatedOverdueDays = updatedDaysTaken - totalNoOfWfhDays;
					log.info("updatedOverdueDays after addition: {}", updatedOverdueDays);
					updatedRemainingDays = 0;
				} else {
					updatedRemainingDays = totalNoOfWfhDays - updatedDaysTaken;
					log.info("updatedRemainingDays after addition: {}", updatedRemainingDays);
					updatedOverdueDays = 0;
				}
			}
		}
		
		else if (dateDifference == 0.0) {
//        	dateDifference = Double.sum(differenceInDays, 1);
			if (wfhRequestDto.startDate().isEqual(wfhRequestDto.endDate())) {
				// If same date need to add the days
				if (wfhDetails.getStartDate().isEqual(wfhDetails.getEndDate())) {
					if (wfhDetails.getRequestedSession().name().equalsIgnoreCase(WorkFromHomeSessionMsg.FULL_DAY.name())
							&& wfhRequestDto.requestedSession().name()
									.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.name())) {
						if (employeeWfhStats.getOverdueWfhDays() > 0.0
								&& employeeWfhStats.getRemainingWfhDays() == 0.0) {
							Double updatedOverdueDay = employeeWfhStats.getOverdueWfhDays() - 0.5;
							employeeWfhStats.setOverdueWfhDays(updatedOverdueDay);
							return (employeeWfhStats);
						} else if (employeeWfhStats.getDaysTaken() > 0.0
								&& employeeWfhStats.getOverdueWfhDays() == 0.0) {
							Double updatedDayTaken = employeeWfhStats.getDaysTaken() - 0.5;
							Double updatedRemainingDay = Double.sum(employeeWfhStats.getRemainingWfhDays(), 0.5);
							employeeWfhStats.setDaysTaken(updatedDayTaken);
							employeeWfhStats.setRemainingWfhDays(updatedRemainingDay);
							return (employeeWfhStats);
						}
					}
					if (wfhRequestDto.requestedSession().getValue()
							.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
						if ((employeeWfhStats.getDaysTaken() > 0.0 && employeeWfhStats.getOverdueWfhDays() == 0.0
								&& employeeWfhStats.getRemainingWfhDays() > 0.0)
								|| (employeeWfhStats.getOverdueWfhDays() == 0.0 && employeeWfhStats.getDaysTaken() == 4
										&& employeeWfhStats.getRemainingWfhDays() == 0.0)) {
							Double updatedHalfDay = Double.sum(employeeWfhStats.getDaysTaken(), 0.5);
							Double updatedRemainingDay = employeeWfhStats.getRemainingWfhDays() - 0.5;
							employeeWfhStats.setDaysTaken(updatedHalfDay);
							employeeWfhStats.setRemainingWfhDays(updatedRemainingDay);
							return (employeeWfhStats);
						} else if (employeeWfhStats.getOverdueWfhDays() > 0.0 && employeeWfhStats.getDaysTaken() > 4.0
								&& employeeWfhStats.getRemainingWfhDays() == 0.0) {
							Double updatedHalfDay = Double.sum(employeeWfhStats.getOverdueWfhDays(), 0.5);
							employeeWfhStats.setOverdueWfhDays(updatedHalfDay);
							employeeWfhStats.setRemainingWfhDays(0.0);
							return (employeeWfhStats);
						}
					} else if (wfhRequestDto.requestedSession().getValue()
							.equalsIgnoreCase(WorkFromHomeSessionMsg.FULL_DAY.getValue())) {
						if ((employeeWfhStats.getDaysTaken() > 0.0 && employeeWfhStats.getOverdueWfhDays() == 0.0
								&& employeeWfhStats.getRemainingWfhDays() > 0.0)
								|| (employeeWfhStats.getOverdueWfhDays() == 0.0 && employeeWfhStats.getDaysTaken() == 4
										&& employeeWfhStats.getRemainingWfhDays() == 0.0)) {
							Double updatedFullDay = Double.sum(employeeWfhStats.getDaysTaken(), 0.5);
							Double updatedRemainingDay = employeeWfhStats.getRemainingWfhDays() - 0.5;
							employeeWfhStats.setDaysTaken(updatedFullDay);
							employeeWfhStats.setRemainingWfhDays(updatedRemainingDay);
							return (employeeWfhStats);
						} else if (employeeWfhStats.getOverdueWfhDays() > 0.0 && employeeWfhStats.getDaysTaken() > 4.0
								&& employeeWfhStats.getRemainingWfhDays() == 0.0) {
							Double updatedFullDay = Double.sum(employeeWfhStats.getDaysTaken(), 0.5);
							employeeWfhStats.setDaysTaken(updatedFullDay);
							Double updatedFullDays = Double.sum(employeeWfhStats.getOverdueWfhDays(), 0.5);
							employeeWfhStats.setOverdueWfhDays(updatedFullDays);
							employeeWfhStats.setRemainingWfhDays(0.0);
							return (employeeWfhStats);
						}
					}
				}
			}
			
		}
		}
		else if (wfhRequestDto.requestedSession().getValue()
				.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue()))
		{
			double halfDayDifference = 0.5;
//			if (updatedOverdueDays > 0) {
//                updatedOverdueDays -= halfDayDifference;
//                updatedDaysTaken -=halfDayDifference;
//            } else {
 
            	if(wfhRequestDto.startDate().isEqual(wfhRequestDto.endDate())) {
//            		updatedDaysTaken -=dateDifference;
            		
                    	 
//      	              long originalDuration = Period.between(wfhDetails.getStartDate(), wfhDetails.getEndDate()).plusDays(1).getDays();
            		 double originalDuration = calculateWeekdaysBetween(wfhDetails.getStartDate(), wfhDetails.getEndDate());
      	              
                    	  double subDay= originalDuration - halfDayDifference;
        	              updatedDaysTaken=updatedDaysTaken-subDay;
//                        updatedRemainingDays = totalNoOfWfhDays - updatedDaysTaken;
                          updatedRemainingDays += subDay;
                          
                          if(updatedDaysTaken>totalNoOfWfhDays) {
                        	  updatedOverdueDays = updatedDaysTaken - totalNoOfWfhDays;
                              updatedRemainingDays = 0;
                          }else {
                        	  updatedRemainingDays =  totalNoOfWfhDays-updatedDaysTaken ;
                        	  updatedOverdueDays = 0;
                          }
    
            		
            	}else {
//	                error 
            	}

		}
		employeeWfhStats.setDaysTaken(updatedDaysTaken);
		employeeWfhStats.setRemainingWfhDays(updatedRemainingDays);
		employeeWfhStats.setOverdueWfhDays(updatedOverdueDays);
		log.info("employeeWfhStats", employeeWfhStats);
		return employeeWfhStats;
	}
	

	public Double fetchDateDifference(WfhRequestDto wfhRequestDto, EmployeeWfhDetails existingWfhDetails) {
		double dateDifference = this.calculateDateDifferences(existingWfhDetails.getStartDate(),
				existingWfhDetails.getEndDate(), wfhRequestDto.startDate(), wfhRequestDto.endDate());
		return ObjectUtils.isNotEmpty(dateDifference) ? dateDifference : 0.0;
	}



	public double calculateDateDifferencesInMonth(WfhRequestDto wfhRequestDto,
			List<EmployeeWfhDetails> getAllexistingWfhDetails) {
		  double totalDuration = 0;
		  
		    for (EmployeeWfhDetails details : getAllexistingWfhDetails) {
		    	if(wfhRequestDto.id()!= details.getId()) {
		        LocalDate originalStartDate = details.getStartDate();
		        LocalDate originalEndDate = details.getEndDate();
		        double duration = Period.between(originalStartDate, originalEndDate).plusDays(1).getDays();
		        totalDuration += duration;
		    	}else {
		    		if(wfhRequestDto.requestedSession().getValue().equalsIgnoreCase(WorkFromHomeSessionMsg.FULL_DAY.getValue())) {
		    			
		    		}else {
		    			   totalDuration += 0.5;
		    		}	
		    		
		    	}
		    }
		
		return totalDuration;
	}

	
//	public long calculateDateDifferences(LocalDate originalStartDate, LocalDate originalEndDate, LocalDate requestStartDate,
//			LocalDate requestEndDate) {
//		log.info("Ems2p0Utility.calculateDateDifferences() - originalStartDate : "+originalStartDate);
//		log.info("Ems2p0Utility.calculateDateDifferences() - originalEndDate : "+originalEndDate);
//		log.info("Ems2p0Utility.calculateDateDifferences() - requestStartDate : "+requestStartDate);
//		log.info("Ems2p0Utility.calculateDateDifferences() - requestEndDate : "+requestEndDate);
//		
//		long originalDuration = Period.between(originalStartDate, originalEndDate).plusDays(1).getDays();
//		
//		log.info("Ems2p0Utility.calculateDateDifferences() - originalDuration : "+originalDuration);
//		
//  
//        long requestDuration = Period.between(requestStartDate, requestEndDate).plusDays(1).getDays();
//        log.info("Ems2p0Utility.calculateDateDifferences() - requestDuration : "+requestDuration);
//        
//        return (requestDuration - originalDuration);
//	}
	public long calculateDateDifferences(LocalDate originalStartDate, LocalDate originalEndDate,
			LocalDate requestStartDate, LocalDate requestEndDate) {
		log.info("Ems2p0Utility.calculateDateDifferences() - originalStartDate : " + originalStartDate);
		log.info("Ems2p0Utility.calculateDateDifferences() - originalEndDate : " + originalEndDate);
		log.info("Ems2p0Utility.calculateDateDifferences() - requestStartDate : " + requestStartDate);
		log.info("Ems2p0Utility.calculateDateDifferences() - requestEndDate : " + requestEndDate);
		double originalDuration = calculateWeekdaysBetween(originalStartDate, originalEndDate);
		log.info("Ems2p0Utility.calculateDateDifferences() - originalDuration : " + originalDuration);
		double requestDuration = calculateWeekdaysBetween(requestStartDate, requestEndDate);
		log.info("Ems2p0Utility.calculateDateDifferences() - requestDuration : " + requestDuration);
		return (long) (requestDuration - originalDuration);
}
}