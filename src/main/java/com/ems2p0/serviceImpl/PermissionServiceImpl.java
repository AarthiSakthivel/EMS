package com.ems2p0.serviceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ems2p0.components.NotificationComponent;
import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.exception.EmployeeNotFound;
import com.ems2p0.dto.exception.PermissionExpiredException;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.response.EmployeeDetailsDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.PermissionDetailsResponseDto;
import com.ems2p0.dto.response.PermissionStatsResponseDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.OfficialRole;
import com.ems2p0.mapper.employee.EmployeeMapper;
import com.ems2p0.mapper.permission.PermissionMapper;
import com.ems2p0.model.EmployeePermissionDetails;
import com.ems2p0.model.EmployeePermissionStats;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.PermissionProjections;
import com.ems2p0.security.multi_factor.MultifactorAuthenticator;
import com.ems2p0.service.PermissionService;
import com.ems2p0.utils.Ems2p0Constants;
import com.ems2p0.utils.Ems2p0Utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 - Service implementation layer which is responsible to do all api
 * operations and business logics.
 *
 * @author Mohan
 * @category Permission module ServiceImpl - Business layer
 * @apiNote - Developer should be responsible to each and every api method will
 *          be simple to read and write and should it should be co ordinating
 *          with utility methods to reuse the logics by maintaining the high
 *          level code quality by reduce the boiler plates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

	/**
	 * Injected the permission mapper to convert the entity to DTO
	 */
	private final PermissionMapper permissionMapper;

	/**
	 * Injected the permission mapper to convert the entity to DTO
	 */
	private final EmployeeMapper employeeMapper;

	/**
	 * Injected the utility component in invoke the re-usable methods
	 */
	private final Ems2p0Utility utility;

	/**
	 * Injected the DAO service to invoke the DB operation methods
	 */
	private final EmsDaoService daoService;

	/**
	 * Injected multi factor authenticator to invoke the OTP validation and
	 * generator methods
	 */
	private final MultifactorAuthenticator multifactorAuthenticator;

	/**
	 * Injected notification component to trigger the notification to employee and
	 * others
	 */
	private final NotificationComponent notificationComponent;

	/**
	 * Method to implement the business logic for the requested or created
	 * permission of the employee
	 */
	@Override
	public GenericResponseDto<PermissionDetailsResponseDto> requestPermission(RequestPermissionDto permissionDto)
			throws Exception {

		String userName = multifactorAuthenticator.getLoggedInUserDetail();

		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);

		UserDetails existsEmployeeData = Optional.ofNullable(daoService.loadUserByUsername(userName))
				.orElseThrow(() -> new EmployeeNotFound("Employee not found...."));
		Optional.ofNullable(daoService.existsByPermissionDetails(existsEmployeeData, permissionDto))
				.ifPresent(permissionDetails -> {
					throw new CustomExceptionDto("Permission already requested for this time and date...!");
				});
		EmployeePermissionStats employeePermissionStats = daoService.findByUserDetailsAndMonth(existsEmployeeData, permissionDto.month());
		EmployeePermissionStats aggregatedPermissionStats = utility.aggregatePermissionTime(permissionDto, employeePermissionStats);
		boolean isEmployeeStatsPresent = ObjectUtils.isNotEmpty(employeePermissionStats)
				&& (ObjectUtils.isNotEmpty(employeePermissionStats.getMonth())
						|| ObjectUtils.isNotEmpty(employeePermissionStats.getId()));
		boolean isOverDue = isEmployeeStatsPresent
				? (ObjectUtils.isNotEmpty(employeePermissionStats.getOverduePermission())
						&& !employeePermissionStats.getOverduePermission().equals(LocalTime.of(0, 0)))
				: (ObjectUtils.isNotEmpty(aggregatedPermissionStats)
						&& ObjectUtils.isNotEmpty(aggregatedPermissionStats.getOverduePermission())
						&& !aggregatedPermissionStats.getOverduePermission().equals(LocalTime.of(0, 0)));
		EmployeePermissionDetails employeePermissionDetails = permissionMapper.toDto(permissionDto, existsEmployeeData, isOverDue);
		EmployeePermissionDetails saveEmployeePermissionDetails = daoService.save(employeePermissionDetails);
		this.sendNotificationForPermission(existsEmployeeData, permissionDto);
		List<EmployeePermissionDetails> employeePermissionDetailsList = new ArrayList<>();
		employeePermissionDetailsList.add(saveEmployeePermissionDetails);
		aggregatedPermissionStats.setMonth(permissionDto.month()).setUserDetails(existsEmployeeData)
				.setPermissionDetails(employeePermissionDetailsList);
				daoService.save(aggregatedPermissionStats);
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, permissionMapper.toRequestPermissionDto(
				saveEmployeePermissionDetails, employee, aggregatedPermissionStats.getHoursTaken()));
	}

	private  synchronized void sendNotificationForPermission(UserDetails existsEmployeeData, RequestPermissionDto permissionDto) {
		notificationComponent.sendNotification(existsEmployeeData, permissionDto, "created");
	}
	/**
	 * Method to implement the business logic for the editing the requested or
	 * created permission of the employee
	 */
	@Override
	public GenericResponseDto<PermissionDetailsResponseDto> editPermission(RequestPermissionDto permissionDto)
			throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();

		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);

		UserDetails existsEmployeeData = daoService.loadUserByUsername(userName);

		EmployeePermissionStats employeePermissionStats = daoService.findByUserDetailsAndMonth(existsEmployeeData, permissionDto.month());

		EmployeePermissionDetails existEmployeePermissionDetails = daoService.findPermissionDetailsByUser(existsEmployeeData,
				permissionDto.id());

		if (permissionDto.date().isEqual(LocalDate.now())) {
			utility.validatePermissionUpdate(existEmployeePermissionDetails.getStartTime());
		}

		if (existEmployeePermissionDetails.getStatus().getValue().equalsIgnoreCase(Ems2p0Status.PENDING.getValue())) {
			long timeDifference = utility.calculateTimeDifference(existEmployeePermissionDetails.getStartTime(),
					existEmployeePermissionDetails.getEndTime(), permissionDto.startTime(), permissionDto.endTime());
			EmployeePermissionStats permissionStats = daoService.updateEditPermissionStatistics(permissionDto,
					timeDifference, employeePermissionStats);
			Boolean isOverDue = (ObjectUtils.isNotEmpty(permissionStats)
					&& ObjectUtils.isNotEmpty(permissionStats.getOverduePermission())
					&& !(permissionStats.getOverduePermission().equals(LocalTime.of(0, 0))));
			this.sendNotificationForPermission(existsEmployeeData, permissionDto);
			EmployeePermissionDetails permissionDetails = permissionMapper.toUpdateDto(permissionDto,
					existEmployeePermissionDetails, existsEmployeeData, isOverDue);
			daoService.save(permissionDetails);
			return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, permissionMapper
					.toRequestPermissionDto(existEmployeePermissionDetails, employee, permissionStats.getHoursTaken()));
		} else {
			throw new PermissionExpiredException("Permission seems not active...");
		}
	}

	/**
	 * Method to implement the business logic for the to fetch the permission
	 * statistics of the employee
	 */
	@Override
	public GenericResponseDto<PermissionStatsResponseDto> fetchPermissionStatsByUsername(String month) {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		UserDetails userDetails = daoService.loadUserByUsername(userName);
		EmployeePermissionStats employeePermissionStats = daoService.findByUserDetailsAndMonth(userDetails, month);
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				(ObjectUtils.isNotEmpty(employeePermissionStats)) ? permissionMapper.toStatsDto(employeePermissionStats)
						: new PermissionStatsResponseDto().setMonth(month).setHoursTaken(LocalTime.of(0, 0))
								.setOverduePermission(LocalTime.of(0, 0)).setRemainingPermission(LocalTime.of(3, 0))
								.setTotalPermission(LocalTime.of(3, 0)));
	}

	/**
	 * Method to implement the business logic for the to fetch the permission
	 * details of the employee
	 */
	@Override
	public GenericResponseDto<List<PermissionDetailsResponseDto>> fetchPermissionDetailsByUsername() {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
		List<EmployeePermissionDetails> employeePermissionDetails = daoService.findPermissionDetByEmployee(Integer.valueOf(employee.getEmpId()));
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				permissionMapper.toListOfDetailsDto(employeePermissionDetails, employee, daoService));
	}

	/**
	 * Method to implement the business logic for the to cancel the permission
	 * requested or created by the employee
	 */
	@Override
	public GenericResponseDto<Long> cancelPermission(UpdatePermissionStatusRequestDto request) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeePermissionDetails existEmployeePermissionDetails = daoService.fetchPermissionDetailsById(request.ids().get(0));
		if (existEmployeePermissionDetails.getDate().isEqual(LocalDate.now())) {
			utility.validatePermissionUpdate(existEmployeePermissionDetails.getStartTime());			
		}
		
		daoService.updatePermissionStatus(Ems2p0Status.CANCELLED, request, userName);
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids().get(0));  
	}

	/**
	 * Method to implement the business logic for the to approve the permission
	 * requested or created by the employee
	 */
	@Override
	public GenericResponseDto<List<Long>> approvePermission(UpdatePermissionStatusRequestDto request) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
	    EmployeeProjection managementEmployee = daoService.loadEmployeeByUsername(userName);
	    daoService.updatePermissionStatus(Ems2p0Status.APPROVED, request, userName);
	     request.ids().forEach(id -> {
	            EmployeePermissionDetails permissionDetails = daoService.fetchPermissionDetailsById(id);
	            notificationComponent.sendApprovalAndRejectionNotification(permissionDetails, managementEmployee,"approved");
	        });
	    return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids());
	}

	/**
	 * Method to implement the business logic for the to reject the permission
	 * requested or created by the employee
	 */
	@Override
	
	public GenericResponseDto<List<Long>> rejectPermission(UpdatePermissionStatusRequestDto request) throws Exception {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
	    EmployeeProjection employee = daoService.loadEmployeeByUsername(userName);
	    daoService.updatePermissionStatus(Ems2p0Status.REJECTED, request, userName);
	      request.ids().forEach(id -> {
	            EmployeePermissionDetails permissionDetails = daoService.fetchPermissionDetailsById(id);
	            notificationComponent.sendApprovalAndRejectionNotification(permissionDetails, employee,"reject");
	        });
	    return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, request.ids());
	}

	/**
	 * Method to implement the business logic for the to fetch the employee
	 * permission details
	 */
	@Override
	public GenericResponseDto<List<PermissionDetailsResponseDto>> fetchEmployeePermissionDetails() {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		UserDetails userDetails = daoService.loadUserByUsername(userName);
		String departmentName = userDetails.getEmployeeRoleManagement().getOfficialRole().name();
		if (StringUtils.equalsIgnoreCase(departmentName, OfficialRole.ROLE_MANAGER.name())) {
			return this.fetchAllEmployeePermissionReports();
		} else {
			List<?> employeeIdsProjections = daoService.fetchByDepartmentNameAndOfficialRole(
					userDetails.getEmployeeRoleManagement().getDepartmentName(), OfficialRole.ROLE_EMPLOYEE);
			List<PermissionProjections> employeePermissionDetailsList = daoService
					.findAllByEmpIds(employeeIdsProjections);
			return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
					permissionMapper.projectionToListOfDetailsDto(employeePermissionDetailsList, daoService));
		}
	} 

	/**
	 * Method to implement the business logic for the to fetch the all employee
	 * permission details and reports
	 */
	@Override
	public GenericResponseDto<List<PermissionDetailsResponseDto>> fetchAllEmployeePermissionReports() {
		List<EmployeePermissionDetails> employeePermissionDetailsList = daoService
				.findAllEmployeesByPermissionDetails();
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				permissionMapper.toListOfPermissionDetailsDto(employeePermissionDetailsList, daoService));
	}

	@Override
	public GenericResponseDto<List<EmployeeDetailsDto>> fetchEmployeeDetail() {
		return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
				employeeMapper.tolistOfBasicDetailsDto(daoService.findAllEmpNameAndId()));
	}

}
