package com.ems2p0.dao.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ems2p0.dto.request.AttendanceDto;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.request.UpdateWfhStatusRequestDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.OfficialRole;
import com.ems2p0.model.EmployeeAttendance;
import com.ems2p0.model.EmployeePermissionDetails;
import com.ems2p0.model.EmployeePermissionStats;
import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.EmployeeWfhStats;
import com.ems2p0.model.MultiFactorAuthentication;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.PermissionProjections;
import com.ems2p0.projections.PermissionStatsProjection;
import com.ems2p0.projections.UserProjection;
import com.ems2p0.projections.WfhProjection;
import com.ems2p0.projections.WorkFromHomeProjectionStats;
import com.ems2p0.projections.WorktypeProjections;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * EMS 2.0 - Interface layer to maintain all API methods and functionalities to
 * hide their business logic and represent the low level visibility to the
 * service implementation level
 *
 * @author Mohan
 * @category DB operations
 * @Version - v1.0.0
 * @apiNote - Developer should be responsible to declare the abstract method
 *          here and should implement the business logic by the serviceImpl
 *          respectively
 */
public interface EmsDaoService {

	UserProjection fetchUserByUserName(String userName);

	EmployeeProjection loadEmployeeByUsername(String userName);

	EmployeeProjection loadEmployeeByEmailId(String emailId);

	UserDetails loadUserByUsername(String userName);

	UserDetails getUserDetailsById(Integer id);
	
	EmployeeWfhDetails findWfhDetailsById(Integer id);

	UserDetails save(UserDetails existsEmployeeData);

	void updatePermissionStatus(Ems2p0Status status, UpdatePermissionStatusRequestDto request, String userName);

	void updateWfhStatus(Ems2p0Status status, UpdateWfhStatusRequestDto request, String userName,EmployeeWfhStats employeeWfhStats);

	EmployeePermissionStats findByUserDetailsAndMonth(UserDetails existsEmployeeData, String month);

	EmployeeWfhStats findByUserDetailsAndMonthForWfhStats(UserDetails exstsEmployeeData, String month);

	PermissionStatsProjection findOverDueAndHoursTakenByUserDetailsAndMonth(String userName, String month);

	WorkFromHomeProjectionStats findOverDueAndDaysTakenByUserDetailsAndMonthForWfh(String userName, String month);

	EmployeePermissionDetails save(EmployeePermissionDetails employeePermissionDetails);

	EmployeeWfhDetails save(EmployeeWfhDetails employeeWfhDetails);

	EmployeePermissionStats save(EmployeePermissionStats employeePermissionStats);

	EmployeeWfhStats save(EmployeeWfhStats employeeWfhStats);

	EmployeePermissionDetails findById(Long id); 

	List<EmployeePermissionStats> findByUserDetails(UserDetails userDetails);

	List<EmployeePermissionDetails> findPermissionDetByEmployee(Integer empId);

	EmployeePermissionDetails fetchPermissionDetailsById(Long id);  

	EmployeeWfhDetails fetchWfhDetailsById(Integer id); 

	List<Integer> fetchByDepartmentNameAndOfficialRole();

	List<Integer> fetchByDepartmentNameAndOfficialRole(String departName, OfficialRole officialRole);

	List<PermissionProjections> findAllByEmpIds(List<?> employeeIdsProjections);
	
	EmployeePermissionDetails existsByPermissionDetails(UserDetails existEmployee, RequestPermissionDto permissionDto);
	
	EmployeeWfhDetails existsByWfhDetails(UserDetails existEmployee, WfhRequestDto wfhRequestDto); 

	MultiFactorAuthentication persistOtp(MultiFactorAuthentication multifactorAuthenticator);

	MultiFactorAuthentication fetchOtp(Integer otp, UserDetails userDetails);

	MultiFactorAuthentication fetchOtpByUserDetailsAndStatus(UserDetails userDetails);

	List<EmployeePermissionDetails> findAllEmployeesByPermissionDetails();

	List<WorktypeProjections> fetchWorktypes();

	List<EmployeeProjection> findAllEmpNameAndId();

	EmployeeAttendance save(EmployeeAttendance attendance);

	EmployeeAttendance fetchCheckIn(@Valid AttendanceDto attendanceDto, String empSerialNo);

	EmployeeAttendance fetchCheckInByUserName(String userName); 

	EmployeePermissionStats updatePermissionStatistics(EmployeePermissionDetails permissionDetails);

	EmployeePermissionStats updateEditPermissionStatistics(RequestPermissionDto permissionDto, long timeDifference,
			EmployeePermissionStats employeePermissionStats);

	EmployeePermissionDetails findPermissionDetailsByUser(UserDetails existsEmployeeData, long id);

	EmployeeWfhDetails findWorkFromHomeDetailsByUser(UserDetails existsEmployeeData, Integer id);

	//List<EmployeeWfhDetails> fetchWorkFromHomeDetails(String employeeId);
	
	List<EmployeeWfhDetails> fetchWorkFromHomeDetails(Integer employeeId);

	List<WfhProjection> findAllWorkFromHomeDetailsByEmpIds(List<?> employeeIdsProjections);

	WorkFromHomeProjectionStats findOverDueAndDaysTakenByUserDetailsAndMonth(String getuserName, String month);

	List<EmployeeWfhDetails> findAllEmployeesByWfhDetails();

	List<String> fetchDeviceTokenByDept(String department,List<String> roles);

	String fetchDeviceTokenByEmployee(String department, String role);

	EmployeeWfhStats updateWfhStatistics(@Valid WfhRequestDto requestDto, long dateDifference,
			EmployeeWfhStats employeeWfhStats);

	List<String> fetchMgmtDeviceToken();

	EmployeeWfhDetails findWfhDetailsById(Long id);

	List<EmployeeWfhDetails> findAllEmployeeByStatus (Ems2p0Status status);
	
	EmployeeWfhStats updateEditPermissionStatistics(WfhRequestDto wfhRequestDto, EmployeeWfhStats employeeWfhStats);

	
	EmployeeWfhStats updateEditWfhStatistics(EmployeeWfhStats employeeWfhStats, EmployeeWfhDetails editEmployeeWfhDetails);
	
//	EmployeeWfhStats updateEditPermissionStatistics(WfhRequestDto wfhRequestDto, EmployeeWfhStats employeeWfhStats);
	
	List<EmployeeWfhDetails> saveAll(List<EmployeeWfhDetails> listOfEmpDetails);

	void deleteAllWfhDetails(String username);

	void deleteAllWfhStats(String username);
	
}
