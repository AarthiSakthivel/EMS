package com.ems2p0.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ems2p0.components.NotificationComponent;
import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.exception.EmployeeNotFound;
import com.ems2p0.dto.request.UpdateWfhStatusRequestDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.WfhDetailsResponseDto;
import com.ems2p0.dto.response.WfhStatsResponseDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.OfficialRole;
import com.ems2p0.enums.WorkFromHomeSessionMsg;
import com.ems2p0.mapper.wfh.WfhMapper;
import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.EmployeeWfhStats;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.WfhProjection;
import com.ems2p0.security.multi_factor.MultifactorAuthenticator;
import com.ems2p0.service.WorkFromHomeService;
import com.ems2p0.utils.Ems2p0Constants;
import com.ems2p0.utils.Ems2p0Utility;

import jakarta.mail.internet.ParseException;
import lombok.RequiredArgsConstructor;

/**
 * EMS 2.0 - Service implementation layer which is responsible to do all api
 * operations and business logics.
 *
 * @author Aarthi Shakthivel
 * @category Work from home module ServiceImpl - Business layer
 * @apiNote - Developer should be responsible to each and every api method will
 *          be simple to read and write and should it should be co ordinating
 *          with utility methods to reuse the logics by maintaining the high
 *          level code quality by reduce the boiler plates.
 */

@Service
@RequiredArgsConstructor
public class WorkFromHomeServiceImpl implements WorkFromHomeService {

	private final EmsDaoService daoService;

	private final MultifactorAuthenticator multifactorAuthenticator;

	private final WfhMapper wfhMapper;

	private final NotificationComponent notificationComponent;

	private final Ems2p0Utility utility;

	private static final Logger logger = LoggerFactory.getLogger(WorkFromHomeServiceImpl.class);

//	private final EmployeeWfhStats employeeWfhStats;

//	public GenericResponseDto<WfhDetailsResponseDto> requestWfh(WfhRequestDto wfhRequestDto) throws Exception {
//		var userName = multifactorAuthenticator.getLoggedInUserDetail();
//
//		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
//
//		var existsEmployeeData = Optional.ofNullable(daoService.loadUserByUsername(userName))
//				.orElseThrow(() -> new EmployeeNotFound("Employee not found...."));
//
//		Optional.ofNullable(daoService.existsByWfhDetails(existsEmployeeData, wfhRequestDto))
//				.ifPresent(permissionDetails -> {
//					throw new CustomExceptionDto("Wfh already requested for this time and session...!");
//				});
//
//		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(existsEmployeeData,
//				wfhRequestDto.month());
//
//		EmployeeWfhStats aggregatedWfhStatistics = utility.aggregateWfhDate(wfhRequestDto, employeeWfhStats);
//
//		var isWfhStatsPresent = ObjectUtils.isNotEmpty(employeeWfhStats)
//				&& (ObjectUtils.isNotEmpty(employeeWfhStats.getMonth())
//						|| ObjectUtils.isNotEmpty(employeeWfhStats.getId()));
//
//		double overDueDaysTaken = 0;
//
//		var isOverDue = isWfhStatsPresent
//				? (ObjectUtils.isNotEmpty(employeeWfhStats.getOverdueWfhDays())
//						&& !(employeeWfhStats.getOverdueWfhDays() == overDueDaysTaken))
//				: (ObjectUtils.isNotEmpty(aggregatedWfhStatistics)
//						&& ObjectUtils.isNotEmpty(aggregatedWfhStatistics.getOverdueWfhDays())
//						&& !(aggregatedWfhStatistics.getOverdueWfhDays() == overDueDaysTaken));
//
//		EmployeeWfhDetails employeeWfhDetails = wfhMapper.toWfhDto(wfhRequestDto, existsEmployeeData, isOverDue,
//				aggregatedWfhStatistics.getDaysTaken());
//
//		EmployeeWfhDetails saveEmployeeWfhDetails = daoService.save(employeeWfhDetails);
//
//		List<EmployeeWfhDetails> employeeWfhDetailsList = new ArrayList<>();
//		employeeWfhDetailsList.add(saveEmployeeWfhDetails);
//
//		aggregatedWfhStatistics.setMonth(wfhRequestDto.month()).setUserDetails(existsEmployeeData)
//				.setWfhDetails(employeeWfhDetailsList);
//		daoService.save(aggregatedWfhStatistics);
//		notificationComponent.sendWfhNotificationForCreateAndEdit(existsEmployeeData, wfhRequestDto,"created");
//		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
//				wfhMapper.toRequestWfhDto(saveEmployeeWfhDetails, employee, aggregatedWfhStatistics.getDaysTaken()));
//	}

	/**
	 * Method to implement the business logic for the requested or created work from
	 * home of the employee
	 */

	@Override
	public GenericResponseDto<WfhDetailsResponseDto> requestWfh(WfhRequestDto wfhRequestDto) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail(); 

		// Load employee data
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
		UserDetails existsEmployeeData = Optional.ofNullable(daoService.loadUserByUsername(userName))
				.orElseThrow(() -> new EmployeeNotFound("Employee not found..."));

		// Check if WFH already requested for the given time and session
		EmployeeWfhDetails getExsistData = daoService.existsByWfhDetails(existsEmployeeData, wfhRequestDto);
		if(ObjectUtils.isNotEmpty(getExsistData)) {
			throw new IllegalArgumentException("Wfh Request is already requested for this date");
		}

		// Load current WFH stats for the month
		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(existsEmployeeData,
				wfhRequestDto.month());
		
		 
	//	EmployeeWfhStats aggregatedWfhStatistics = utility.aggregateWfhDate(wfhRequestDto, employeeWfhStats).keySet().iterator().next();
		
		Map<EmployeeWfhStats, Double> aggregatedWfhStatistics = utility.aggregateWfhDate(wfhRequestDto, employeeWfhStats);
		
		EmployeeWfhStats getEmployeeStats = aggregatedWfhStatistics.keySet().iterator().next();
				
	    double noofDays	= aggregatedWfhStatistics.get(getEmployeeStats);

		EmployeeWfhStats resultOfAggregatedWfhStats = ObjectUtils.isNotEmpty(employeeWfhStats)? employeeWfhStats : getEmployeeStats;
		
		boolean isWfhStatsPresent = ObjectUtils.isNotEmpty(employeeWfhStats)
				&& (ObjectUtils.isNotEmpty(employeeWfhStats.getMonth())
						|| ObjectUtils.isNotEmpty(employeeWfhStats.getId()));

		double overDueDaysTaken = 0;

		boolean isOverDue = isWfhStatsPresent
				? (ObjectUtils.isNotEmpty(employeeWfhStats.getOverdueWfhDays())
						&& employeeWfhStats.getOverdueWfhDays() != overDueDaysTaken)
				: (ObjectUtils.isNotEmpty(getEmployeeStats)
						&& ObjectUtils.isNotEmpty(getEmployeeStats.getOverdueWfhDays())
						&& getEmployeeStats.getOverdueWfhDays() != overDueDaysTaken);
         
		//double noofDays = utility.aggregateWfhDate(wfhRequestDto, employeeWfhStats).get(resultOfAggregatedWfhStats);
		
		// Map WFH request to entity and save 
		EmployeeWfhDetails employeeWfhDetails = wfhMapper.toWfhDto(wfhRequestDto, existsEmployeeData, isOverDue, noofDays);
		EmployeeWfhDetails saveEmployeeWfhDetails = daoService.save(employeeWfhDetails);
        this.sendSynchronizeWfhNotification(existsEmployeeData,wfhRequestDto);

		// Update and save aggregated WFH statistics
		List<EmployeeWfhDetails> employeeWfhDetailsList = new ArrayList<>();
		employeeWfhDetailsList.add(saveEmployeeWfhDetails);
		getEmployeeStats.setMonth(wfhRequestDto.month()).setUserDetails(existsEmployeeData)
				.setWfhDetails(employeeWfhDetailsList); 

		daoService.save(getEmployeeStats);

		

		// Return response
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				wfhMapper.toRequestWfhDto(saveEmployeeWfhDetails, employee, getEmployeeStats.getDaysTaken()));
	}

	private  synchronized void sendSynchronizeWfhNotification(UserDetails existsEmployeeData, WfhRequestDto wfhRequestDto) {
		notificationComponent.sendWfhNotificationForCreateAndEdit(existsEmployeeData, wfhRequestDto, "created");
		}

	/**
	 * Method to implement the business logic for the to fetch the work from home
	 * statistics of the employee
	 */

	public GenericResponseDto<WfhStatsResponseDto> fetchWorkFromStatsByUsername(String month) {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		UserDetails userDetails = daoService.loadUserByUsername(userName);
		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(userDetails, month);
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				(ObjectUtils.isNotEmpty(employeeWfhStats)) ? wfhMapper.toStatsDto(employeeWfhStats)
						: new WfhStatsResponseDto().setMonth(month).setDaysTaken(0).setOverdueWfhDays(0)
								.setRemainingWfhDays(0).setTotalWfhDays(4));
	}

	/**
	 * Method to implement the business logic for the to cancel the work from home
	 * requested or created by the employee
	 */

	@Override
	public GenericResponseDto<Integer> cancelWfh(UpdateWfhStatusRequestDto request) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeWfhDetails existEmployeeWfhDetails = daoService.fetchWfhDetailsById(request.ids().get(0));
		UserDetails existsEmployeeData = daoService.loadUserByUsername(userName);
		// getting existing record in statistics table
		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(existsEmployeeData,
				existEmployeeWfhDetails.getMonth());
		daoService.updateWfhStatus(Ems2p0Status.CANCELLED, request, userName, employeeWfhStats);
		utility.validateWorkFromHomeUpdate(existEmployeeWfhDetails);
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids().get(0));
	}

	/**
	 * Method to implement the business logic for the editing the requested or
	 * created work from home of the employee
	 */

	@Override
	public GenericResponseDto<WfhDetailsResponseDto> editWfhRequest(WfhRequestDto wfhRequestDto)
			throws ParseException, java.text.ParseException {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
		// Getting employee Data Based on the
		UserDetails existsEmployeeData = Optional.ofNullable(daoService.loadUserByUsername(userName))
				.orElseThrow(() -> new EmployeeNotFound("Employee not found..."));
		// getting existing record in statistics table
		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(existsEmployeeData,
				wfhRequestDto.month());
		EmployeeWfhDetails existingWfhDetails = daoService.findWorkFromHomeDetailsByUser(existsEmployeeData,
				wfhRequestDto.id());
//	                .orElseThrow(() -> new CustomExceptionDto("WFH request not found for the given ID."));
		if (!existingWfhDetails.getUserDetails().getUsername().equals(userName)) {
			throw new CustomExceptionDto("Unauthorized attempt to edit WFH request");
		}

		// getting the existing data in Details Table
		Optional.ofNullable(daoService.existsByWfhDetails(existsEmployeeData, wfhRequestDto))
				.ifPresent(permissionDetails -> {
					if (!permissionDetails.getId().equals(wfhRequestDto.id())) {
						throw new CustomExceptionDto("WFH already requested for this time and session...!");
					}
				});

		if (existingWfhDetails.getStatus().getValue().equalsIgnoreCase(Ems2p0Status.PENDING.getValue())) {

			double dateDifference = 0.0;
			double newUpdatedNoOfDays =0.0;

			if (wfhRequestDto.requestedSession().getValue().equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())
					&& wfhRequestDto.startDate().isEqual(wfhRequestDto.endDate())) {
				dateDifference = 0.5;
//			dateDifference = utility.calculateDateDifferencesForHalfDay(existingWfhDetails.getStartDate(),
//						existingWfhDetails.getEndDate(), wfhRequestDto.startDate(), wfhRequestDto.endDate());/

			} else if (wfhRequestDto.requestedSession().getValue()
					.equalsIgnoreCase(WorkFromHomeSessionMsg.FULL_DAY.getValue())) {
				dateDifference = utility.calculateDateDifferences(existingWfhDetails.getStartDate(),
						existingWfhDetails.getEndDate(), wfhRequestDto.startDate(), wfhRequestDto.endDate());
			}
			EmployeeWfhStats aggregatedWfhStatistics = utility.aggregateWfhDateForEdits(wfhRequestDto,
					existingWfhDetails, employeeWfhStats, dateDifference);
			if(wfhRequestDto.requestedSession().getValue()
					.equalsIgnoreCase(WorkFromHomeSessionMsg.HALF_DAY.getValue())) {
				dateDifference = 0.5;
				double diff =  existingWfhDetails.getNoOfDays() - dateDifference;
//				 newUpdatedNoOfDays = existingWfhDetails.getNoOfDays() - diff;
				newUpdatedNoOfDays = dateDifference;
			} 
		
			else if( wfhRequestDto.requestedSession().getValue()
					.equalsIgnoreCase(WorkFromHomeSessionMsg.FULL_DAY.getValue())) {
				
				
				double diff =  existingWfhDetails.getNoOfDays();
				
				double requestDuration = calculateWeekdaysBetween(wfhRequestDto.startDate(), wfhRequestDto.endDate());
				//newUpdatedNoOfDays=  Math.abs(requestDuration-diff);
				newUpdatedNoOfDays = requestDuration;
				
				
//				 newUpdatedNoOfDays = (dateDifference <= 0) ? Math.abs(dateDifference) - (existingWfhDetails.getNoOfDays())
//					 :(dateDifference - existingWfhDetails.getNoOfDays());
			}
			
			boolean isWfhStatsPresent = ObjectUtils.isNotEmpty(employeeWfhStats)
					&& (ObjectUtils.isNotEmpty(employeeWfhStats.getMonth())
							|| ObjectUtils.isNotEmpty(employeeWfhStats.getId()));
			double overDueDaysTaken = 0;
			boolean isOverDue = isWfhStatsPresent
					? (ObjectUtils.isNotEmpty(employeeWfhStats.getOverdueWfhDays())
							&& !(employeeWfhStats.getOverdueWfhDays() == overDueDaysTaken))
					: (ObjectUtils.isNotEmpty(aggregatedWfhStatistics)
							&& ObjectUtils.isNotEmpty(aggregatedWfhStatistics.getOverdueWfhDays())
							&& !(aggregatedWfhStatistics.getOverdueWfhDays() == overDueDaysTaken));
//	          EmployeeWfhStats employeeWfhStatsUpdate = daoService.updateEditWfhStatistics(employeeWfhStats);
			EmployeeWfhDetails updatedRequest = wfhMapper.updateWfhDto(existingWfhDetails, existsEmployeeData,
					wfhRequestDto, isOverDue,(newUpdatedNoOfDays < 0 ? Math.abs(newUpdatedNoOfDays) : newUpdatedNoOfDays));
			  this.sendSynchronizeWfhNotification(existsEmployeeData,wfhRequestDto);
			EmployeeWfhDetails updatedEmployeeWfhDetails = daoService.save(updatedRequest);	
			List<EmployeeWfhDetails> employeeWfhDetailsList = new ArrayList<>();
			employeeWfhDetailsList.add(updatedEmployeeWfhDetails);
			aggregatedWfhStatistics.setMonth(wfhRequestDto.month()).setUserDetails(existingWfhDetails.getUserDetails())
					.setWfhDetails(employeeWfhDetailsList);
			daoService.save(aggregatedWfhStatistics);
		//	notificationComponent.sendWfhNotificationForCreateAndEdit(existsEmployeeData, wfhRequestDto, "edited");
			
			return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, wfhMapper
					.toRequestWfhDto(updatedEmployeeWfhDetails, employee, aggregatedWfhStatistics.getDaysTaken()));
		}
		return new GenericResponseDto<>(false, "WFH request cannot be edited because it is not in pending status.",
				null);
	}
	

	/**
	 * Method to implement the business logic for the to fetch the work from home
	 * details of the employee
	 */

	@Override
	public GenericResponseDto<List<WfhDetailsResponseDto>> fetchWorkFromHomeDetailsByUsername() {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
		List<EmployeeWfhDetails> workFromHomeDetails = daoService.fetchWorkFromHomeDetails(Integer.valueOf(employee.getEmpId()));
		List<WfhDetailsResponseDto> responseList = wfhMapper.tolistOfDetails(workFromHomeDetails, employee, daoService);
		return new GenericResponseDto<List<WfhDetailsResponseDto>>(true, Ems2p0Constants.SUCCESS, responseList);
	}

	/**
	 * Method to implement the business logic for the to fetch the employee work
	 * from home details
	 */

	@Override
	public GenericResponseDto<List<WfhDetailsResponseDto>> fetchEmployeesWorkFromHomeDetailsByUsername() {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		UserDetails userDetails = daoService.loadUserByUsername(userName);
		String role = userDetails.getEmployeeRoleManagement().getOfficialRole().name();
		if (StringUtils.equalsIgnoreCase(role, OfficialRole.ROLE_MANAGER.name()) 
				|| StringUtils.equalsIgnoreCase(role, OfficialRole.ROLE_ADMIN.name())) {
			return this.fetchAllEmployeeWfhReports(); 

		} else {
			List<?> employeeIdsProjections = daoService.fetchByDepartmentNameAndOfficialRole(
					userDetails.getEmployeeRoleManagement().getDepartmentName(), OfficialRole.ROLE_EMPLOYEE);
			List<WfhProjection> employeeWorkFromHomeDetails = daoService
					.findAllWorkFromHomeDetailsByEmpIds(employeeIdsProjections);
			return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
					wfhMapper.projectionToListOfDetailsDto(employeeWorkFromHomeDetails, daoService));
		}
	}

	/**
	 * Method to implement the business logic for the to fetch the all employee work
	 * from home details and reports
	 */

	@Override
	public GenericResponseDto<List<WfhDetailsResponseDto>> fetchAllEmployeeWfhReports() {
		List<EmployeeWfhDetails> employeeWfhDetailsList = daoService.findAllEmployeesByWfhDetails();
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				wfhMapper.toListOfWfhDetailsDto(employeeWfhDetailsList, daoService));
	}

	/**
	 * Method to implement the business logic for the to approve the work from home
	 * requested or created by the employee
	 */

	@Override

	public GenericResponseDto<List<Integer>> approveWfh(UpdateWfhStatusRequestDto request) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeProjection managementEmployee = daoService.loadEmployeeByUsername(userName);
		UserDetails existsEmployeeData = Optional.ofNullable(daoService.loadUserByUsername(userName))
				.orElseThrow(() -> new EmployeeNotFound("Employee not found..."));
		EmployeeWfhDetails existEmployeeWfhDetails = daoService.fetchWfhDetailsById(request.ids().get(0));
		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(existsEmployeeData,
				existEmployeeWfhDetails.getMonth());
		daoService.updateWfhStatus(Ems2p0Status.APPROVED, request, userName, employeeWfhStats);
		request.ids().forEach(id -> {
			EmployeeWfhDetails employeeWfhDetails = daoService.fetchWfhDetailsById(id);
			notificationComponent.sendApprovalAndRejectionWfhNotification(employeeWfhDetails, managementEmployee,
					"approved");
		});
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids());
	}

	/**
	 * Method to implement the business logic for the to reject the work from home
	 * requested or created by the employee
	 */

//	@Override
//	
// 	public GenericResponseDto<List<Long>> rejectWfh(UpdateWfhStatusRequestDto request) throws Exception {
//	    var userName = multifactorAuthenticator.getLoggedInUserDetail();
//	    EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
//	    daoService.updateWfhStatus(Ems2p0Status.REJECTED, request, userName);
//	    request.ids().forEach(id ->{
//	       EmployeeWfhDetails employeeWfhDetails =daoService.fetchWfhDetailsById(id);
//	       notificationComponent.sendApprovalAndRejectionWfhNotification(employeeWfhDetails,employee,"rejected");
//	    });
//	    return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids());
//	}

	@Override
	public GenericResponseDto<List<Integer>> rejectWfh(UpdateWfhStatusRequestDto request) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
//		 var existsEmployeeData = Optional.ofNullable(daoService.loadUserByUsername(userName))
// 	             .orElseThrow(() -> new EmployeeNotFound("Employee not found..."));
		EmployeeWfhDetails existingWfhDetails = daoService.findWfhDetailsById(request.ids().get(0));
		// getting existing record in statistics table
		EmployeeWfhStats employeeWfhStats = daoService.findByUserDetailsAndMonthForWfhStats(
				existingWfhDetails.getUserDetails(), existingWfhDetails.getMonth());
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
		daoService.updateWfhStatus(Ems2p0Status.REJECTED, request, userName, employeeWfhStats);
		request.ids().forEach(id -> {
			EmployeeWfhDetails employeeWfhDetails = daoService.fetchWfhDetailsById(id);
			notificationComponent.sendApprovalAndRejectionWfhNotification(employeeWfhDetails, employee, "rejected");
		});
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids());
	}
	
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

	@Override
	public void clearWfhData() {
		String username = multifactorAuthenticator.getLoggedInUserDetail();
		daoService.deleteAllWfhDetails(username);
		daoService.deleteAllWfhStats(username);
	}
}
