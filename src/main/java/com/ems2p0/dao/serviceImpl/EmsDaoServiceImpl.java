package com.ems2p0.dao.serviceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.exception.EmployeeNotFound;
import com.ems2p0.dto.exception.PermissionNotFound;
import com.ems2p0.dto.exception.WfhNotFound;
import com.ems2p0.dto.request.AttendanceDto;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.request.UpdateWfhStatusRequestDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.ExceptionMsg;
import com.ems2p0.enums.OfficialRole;
import com.ems2p0.enums.OtpStatus;
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
import com.ems2p0.repo.AuthenticationRepository;
import com.ems2p0.repo.EmployeeAttendanceRepo;
import com.ems2p0.repo.EmployeePermissionDetailsRepo;
import com.ems2p0.repo.EmployeePermissionStatsRepo;
import com.ems2p0.repo.EmployeeWfhDetailsRepo;
import com.ems2p0.repo.EmployeeWfhStatsRepo;
import com.ems2p0.repo.MultiFactorAuthenticatorRepo;
import com.ems2p0.repo.UserDetailsRepository;
import com.ems2p0.utils.Ems2p0Utility;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 - Service implementation layer which is responsible to do all API
 * operations and business logics.
 *
 * @author Mohan
 * @category DB operations module ServiceImpl - Business layer
 * @apiNote - Developer should be responsible to each and every API method will
 *          be simple to read and write and should it should be coordinating
 *          with utility methods to reuse the logics by maintaining the high
 *          level code quality by reduce the boiler plates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmsDaoServiceImpl implements EmsDaoService {

	private final UserDetailsRepository userDetailsRepository;

	private final EmployeePermissionDetailsRepo permissionDetailsRepo;

	private final EmployeePermissionStatsRepo permissionStatsRepo;

	private final AuthenticationRepository authenticationRepository;

	private final EmployeeAttendanceRepo attendanceRepo;

	private final MultiFactorAuthenticatorRepo multiFactorAuthenticatorRepo;

	private final Ems2p0Utility utility;

	private final EmployeeWfhDetailsRepo employeeWfhDetailRepo;

	private final EmployeeWfhStatsRepo employeeWfhStatsRepo;

	private EmsDaoService daoService;

	/**
	 * Method to fetch the user by userName
	 */
	@Override
	public UserProjection fetchUserByUserName(String userName) {
		return authenticationRepository.findByUserName(userName)
				.orElseThrow(() -> new EmployeeNotFound("User not found"));
	}

	/**
	 * Method to load the employee by userName
	 */
	@Override
	public EmployeeProjection loadEmployeeByUsername(String userName) {
		return authenticationRepository.findEmployeeByUserName(userName)
				.orElseThrow(() -> new EmployeeNotFound(ExceptionMsg.EMPLOYEE_NOT_FOUND.getMessage()));
	}

	/**
	 * Method to load the employee by emailId
	 */
	@Override
	public EmployeeProjection loadEmployeeByEmailId(String emailId) {
		return authenticationRepository.findEmployeeByEmailId(emailId)
				.orElseThrow(() -> new EmployeeNotFound(ExceptionMsg.EMPLOYEE_NOT_FOUND.getMessage()));
	}

	/**
	 * Method to load the user details by userName
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		return userDetailsRepository.findByUserName(userName)
				.orElseThrow(() -> new EmployeeNotFound(ExceptionMsg.EMPLOYEE_NOT_FOUND.getMessage()));
	}

	/**
	 * Method to fetch the user details by Id
	 */
	@Override
	public UserDetails getUserDetailsById(Integer id) {
		return userDetailsRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFound(ExceptionMsg.EMPLOYEE_NOT_FOUND.getMessage()));
	}

	/**
	 * Method to persist the user details
	 */
	@Override
	public UserDetails save(UserDetails existsEmployeeData) {
		return userDetailsRepository.save(existsEmployeeData);
	}

	/**
	 * Method to fetch the employee's permission statistics by user details and
	 * month
	 */
	@Override
	public EmployeePermissionStats findByUserDetailsAndMonth(UserDetails existsEmployeeData, String month) {
		return permissionStatsRepo.findByUserDetailsAndMonth(existsEmployeeData, month).orElse(null);
	}

	@Override
	public EmployeeWfhStats findByUserDetailsAndMonthForWfhStats(UserDetails existsEmployeeData, String month) {
		return employeeWfhStatsRepo.findByUserDetailsAndMonth(existsEmployeeData, month).orElse(null);
	}

	@Override
	public PermissionStatsProjection findOverDueAndHoursTakenByUserDetailsAndMonth(String userName, String month) {
		return permissionStatsRepo.findOverDueAndHoursTakenByUserDetailsAndMonth(userName, month);
	}

	@Override
	public WorkFromHomeProjectionStats findOverDueAndDaysTakenByUserDetailsAndMonthForWfh(String userName,
			String month) {
		return employeeWfhStatsRepo.findOverDueAndDaysTakenByUserDetailsAndMonth(userName, month);
	}

	/**
	 * Method to persist the employee's permission details
	 */
	@Override
	public EmployeePermissionDetails save(EmployeePermissionDetails employeePermissionDetails) {
		return permissionDetailsRepo.save(employeePermissionDetails);
	}

	@Override
	public EmployeeWfhDetails save(EmployeeWfhDetails employeeWfhDetails) {
		return employeeWfhDetailRepo.save(employeeWfhDetails);
	}

	/**
	 * Method to persist the employee's permission statistics details
	 */
	@Override
	public EmployeePermissionStats save(EmployeePermissionStats employeePermissionStats) {
		return permissionStatsRepo.save(employeePermissionStats);
	}

	@Override
	public EmployeeWfhStats save(EmployeeWfhStats employeeWfhStats) {
		return employeeWfhStatsRepo.save(employeeWfhStats);
	}

	/**
	 * Method to fetch the employee's permission details by Id
	 */
	@Override
	public EmployeePermissionDetails findById(Long id) {
		return permissionDetailsRepo.findById(id).orElseThrow(() -> new PermissionNotFound("Permission not found.."));
	}

	/**
	 * Method to fetch the user details by user details
	 */
	@Override
	public List<EmployeePermissionStats> findByUserDetails(UserDetails userDetails) {
		return permissionStatsRepo.findByUserDetails(userDetails);
	}

	/**
	 * Method to fetch the user details by empId
	 */
	@Override
	public List<EmployeePermissionDetails> findPermissionDetByEmployee(Integer empId) {
		return permissionDetailsRepo.findPermissionDetByEmployee(empId);
	}

	/**
	 * Method to fetch the permission details by Id
	 */
	@Override
	public EmployeePermissionDetails fetchPermissionDetailsById(Long id) {
		return permissionDetailsRepo.findById(id).orElseThrow(() -> new PermissionNotFound("Not found"));
	}

	@Override
	public EmployeeWfhDetails fetchWfhDetailsById(Integer id) {
		return employeeWfhDetailRepo.findById(id).orElseThrow(() -> new WfhNotFound("Not found"));
	}

	/**
	 * Method to fetch the employee ids by department name and role
	 */
	@Override
	public List<Integer> fetchByDepartmentNameAndOfficialRole() {
		return userDetailsRepository.fetchByDepartmentNameAndOfficialRole();
	}

	@Override
	public List<Integer> fetchByDepartmentNameAndOfficialRole(String departName, OfficialRole officialRole) {
		return userDetailsRepository.fetchByDepartmentNameAndOfficialRole(departName,
				ObjectUtils.isNotEmpty(officialRole) ? officialRole.name() : "");
	}

	@Override
	public EmployeeWfhDetails findWfhDetailsById(Integer id) {
		return employeeWfhDetailRepo.findWfhDetailsById(id);
	}

	/**
	 * Method to fetch the permission project details by all employee's ids
	 */
	@Override
	public List<PermissionProjections> findAllByEmpIds(List<?> employeeIdsProjections) {
		return permissionDetailsRepo.findAllByEmpIds(employeeIdsProjections);
	}

	/**
	 * Method to update the employee permission details and status
	 */
	@Override
	public void updatePermissionStatus(Ems2p0Status status, UpdatePermissionStatusRequestDto request, String userName) {
		this.loadEmployeeByUsername(userName);
		request.ids().stream().filter(Objects::nonNull).forEach(id -> {
			EmployeePermissionDetails editEmployeePermissionDetails = this.fetchPermissionDetailsById(id);
			this.validatePermissionStatus(editEmployeePermissionDetails, status);
			editEmployeePermissionDetails.setStatus(status);
			this.save(editEmployeePermissionDetails);
			Set<Ems2p0Status> statusToUpdateStats = EnumSet.of(Ems2p0Status.CANCELLED, Ems2p0Status.REJECTED);
			if (statusToUpdateStats.contains(status)) {
				this.updatePermissionStatistics(editEmployeePermissionDetails);
			}
		});
	}

	@Override
	public EmployeeWfhStats updateEditWfhStatistics(EmployeeWfhStats employeeWfhStats,
			EmployeeWfhDetails editEmployeeWfhDetails) {

		double remainingWfhDays = employeeWfhStats.getRemainingWfhDays();

		double overDueWfhDays = employeeWfhStats.getOverdueWfhDays();

		double daysTaken = employeeWfhStats.getDaysTaken();

		double noOfDays = editEmployeeWfhDetails.getNoOfDays();

		double updatedOverDueWfhDays = 0.0;

		double updatedDaysTaken = 0.0;

		double updatedRemainingWfhDays = 0.0;

		// Updating condition if overdue was taken
		if (!(overDueWfhDays == 0.0) && (remainingWfhDays == 0.0)) {
			if (!(daysTaken == 0.0)) {
				updatedOverDueWfhDays = Math.abs(overDueWfhDays - noOfDays);
				updatedDaysTaken = (noOfDays - daysTaken) < 0 ? (daysTaken - noOfDays) : (noOfDays - daysTaken);
				if (updatedOverDueWfhDays == 0.0) {
					updatedDaysTaken = (noOfDays - daysTaken) < 0 ? (daysTaken - noOfDays) : (noOfDays - daysTaken);
					updatedRemainingWfhDays = (updatedDaysTaken == 0.0 && updatedOverDueWfhDays == 0.0) ? 4
							: updatedRemainingWfhDays;
					return this.saveWfhStats(employeeWfhStats, updatedOverDueWfhDays, updatedRemainingWfhDays,
							updatedDaysTaken);
				} else {
					updatedRemainingWfhDays = (updatedDaysTaken == 0.0 && updatedOverDueWfhDays == 0.0) ? 4
							: updatedRemainingWfhDays;
					return this.saveWfhStats(employeeWfhStats, updatedOverDueWfhDays, updatedRemainingWfhDays,
							updatedDaysTaken);
				}
			}
			// Updating condition if daystaken
		} else if ((remainingWfhDays > 0.0) && (overDueWfhDays == 0.0)
				|| (remainingWfhDays == 0.0) && (daysTaken > 0.0)) {
			updatedDaysTaken = Math.abs(daysTaken - noOfDays);
			updatedRemainingWfhDays = noOfDays + remainingWfhDays;
			return this.saveWfhStats(employeeWfhStats, updatedOverDueWfhDays, updatedRemainingWfhDays,
					updatedDaysTaken);
		}
		return employeeWfhStats;
	}

	private EmployeeWfhStats saveWfhStats(EmployeeWfhStats employeeWfhStats, double updatedOverDueWfhDays,
			double updatedRemainingWfhDays, double updatedDaysTaken) {
		employeeWfhStats.setOverdueWfhDays(updatedOverDueWfhDays);
		employeeWfhStats.setRemainingWfhDays(updatedRemainingWfhDays);
		employeeWfhStats.setDaysTaken(updatedDaysTaken);
		EmployeeWfhStats returnWfhStats = this.save(employeeWfhStats);
		return returnWfhStats;
	}

	@Override
	public void updateWfhStatus(Ems2p0Status status, UpdateWfhStatusRequestDto request, String userName,
			EmployeeWfhStats employeeWfhStats) {
		this.loadEmployeeByUsername(userName);
		request.ids().stream().filter(Objects::nonNull).forEach(id -> {
			EmployeeWfhDetails editEmployeeWfhDetails = this.fetchWfhDetailsById(id);
			this.validateWfhStatus(editEmployeeWfhDetails, status);
			editEmployeeWfhDetails.setStatus(status);
			this.save(editEmployeeWfhDetails);
			Set<Ems2p0Status> statusToUpdateStats = EnumSet.of(Ems2p0Status.CANCELLED, Ems2p0Status.REJECTED);
			if (statusToUpdateStats.contains(status)) {
				this.updateEditWfhStatistics(employeeWfhStats, editEmployeeWfhDetails);
				List<EmployeeWfhDetails> listOfEmpDetails = this.findAllEmployeeByStatus(Ems2p0Status.PENDING);
				listOfEmpDetails.stream().filter(employeeWfhDetails -> ObjectUtils.isNotEmpty(employeeWfhDetails))
						.map(empDetail -> {
							if (ObjectUtils.isNotEmpty(empDetail.getIsOverDue())
									&& ObjectUtils.isNotEmpty(employeeWfhStats)
									&& ObjectUtils.isNotEmpty(employeeWfhStats.getDaysTaken())
									&& ObjectUtils.isNotEmpty(employeeWfhStats.getTotalWfhDays())) {
								if (employeeWfhStats.getDaysTaken() <= employeeWfhStats.getTotalWfhDays()
										|| employeeWfhStats.getDaysTaken() == 0) {
									empDetail.setIsOverDue(false);
								} else {
									empDetail.setIsOverDue(true);
								}
							}
							return empDetail;
						}).collect(Collectors.toList());
				this.saveAll(listOfEmpDetails);
				log.info("Saved all Pending employee Wfh Details list :{}", listOfEmpDetails.size());
			}
		});
	}

	public List<EmployeeWfhDetails> saveAll(List<EmployeeWfhDetails> listOfEmpDetails) {
		return employeeWfhDetailRepo.saveAll(listOfEmpDetails);
	}

	/**
	 * Method to validate the permission status
	 *
	 * @param id
	 */
	private void validatePermissionStatus(EmployeePermissionDetails employeePermissionDetails, Ems2p0Status status) {
//		if (employeePermissionDetails.getStatus().name().equals(Ems2p0Status.REJECTED.name())) {
//			throw new CustomExceptionDto(
//					"Already Rejected permission cannot be reject... please cancel and request again");
//		}
		if (employeePermissionDetails.getStatus().name().equals(Ems2p0Status.CANCELLED.name())
				|| employeePermissionDetails.getStatus().name().equals(Ems2p0Status.REJECTED.name())) {
			throw new CustomExceptionDto(
					"Permission is not active to update... It might already cancelled or rejected");
		}
	}

	private void validateWfhStatus(EmployeeWfhDetails editEmployeeWfhDetails, Ems2p0Status status) {
//		if (editEmployeeWfhDetails.getStatus().name().equals(Ems2p0Status.REJECTED.name())){
//			throw new CustomExceptionDto(
//					"Already approved Work From Home Request cannot be reject... please cancel and request again");
//		}
		if (editEmployeeWfhDetails.getStatus().name().equals(Ems2p0Status.CANCELLED.name())
				|| editEmployeeWfhDetails.getStatus().name().equals(Ems2p0Status.REJECTED.name()) 
				||editEmployeeWfhDetails.getStatus().name().equals(Ems2p0Status.APPROVED.name())) {
			throw new CustomExceptionDto(
					"Work From Home request is not active to update... It might already cancelled, rejected or approved");
		}
	}

	/**
	 * Method to fetch the permission exist by employee with start time and date
	 */
	@Override
	public EmployeePermissionDetails existsByPermissionDetails(UserDetails existEmployee,
			RequestPermissionDto permissionDto) {
		return permissionDetailsRepo.findByUserDetailsAndStartTimeAndDateAndStatus(existEmployee,
				permissionDto.startTime(), permissionDto.date(), Ems2p0Status.PENDING);
	}

	@Override
	public EmployeeWfhDetails existsByWfhDetails(UserDetails existEmployee, WfhRequestDto wfhRequestDto) {
		return employeeWfhDetailRepo.findByStartDateOrEndDateAndStatusNotAndUserDetails_EmpId(wfhRequestDto.startDate(),
				wfhRequestDto.endDate(),existEmployee.getEmpId());    
	}
 
	/**
	 * Method to save the OTP by flush method
	 */
	@Override
	public MultiFactorAuthentication persistOtp(MultiFactorAuthentication multiFactorAuthentication) {
		return multiFactorAuthenticatorRepo.saveAndFlush(multiFactorAuthentication);
	}

	/**
	 * Method to fetch the OTP by user details
	 */
	@Override
	public MultiFactorAuthentication fetchOtp(Integer otp, UserDetails userDetails) {
		return multiFactorAuthenticatorRepo.findByOtpAndOtpStatusAndUserDetails(otp, OtpStatus.ACTIVE, userDetails)
				.orElseThrow(() -> new CustomExceptionDto("Invalid OTP....."));
	}

	/**
	 * Method to fetch the OTP by user details and status
	 */
	@Override
	public MultiFactorAuthentication fetchOtpByUserDetailsAndStatus(UserDetails userDetails) {
		return multiFactorAuthenticatorRepo.findByUserDetails(userDetails);
	}

	/**
	 * Method to fetch all employee permission details
	 */
	@Override
	public List<EmployeePermissionDetails> findAllEmployeesByPermissionDetails() {
		return permissionDetailsRepo.findAllEmployeesByPermissionDetails();
	}

	@Override
	public List<EmployeeWfhDetails> findAllEmployeesByWfhDetails() {
		return employeeWfhDetailRepo.findAllEmployeesByWfhDetails();
	}

	/**
	 * Method to fetch all work types
	 */
	@Override
	public List<WorktypeProjections> fetchWorktypes() {
		return attendanceRepo.findAllWorktypes();
	}

	/**
	 * Method to fetch all employee's basic details
	 */
	@Override
	public List<EmployeeProjection> findAllEmpNameAndId() {
		return permissionDetailsRepo.findAllEmpNameAndId();
	}

	/**
	 * Method to fetch save the attendance details
	 */
	@Override
	public EmployeeAttendance save(EmployeeAttendance attendance) {
		return attendanceRepo.save(attendance);
	}

	/**
	 * Method to fetch the check-in record details
	 */
	@Override
	public EmployeeAttendance fetchCheckIn(@Valid AttendanceDto attendanceDto, String empSerialNo) {
		EmployeeAttendance fetchLastCheckinRecord = attendanceRepo.findByEmpidAndRegisterid(empSerialNo,
				attendanceDto.last_Id());
		return ObjectUtils.isNotEmpty(fetchLastCheckinRecord) ? fetchLastCheckinRecord : null;
	}

	/**
	 * Method to fetch the check-in details by userName
	 */
	@Override
	public EmployeeAttendance fetchCheckInByUserName(String userName) {
		return attendanceRepo.findByCheckInRecordByNames(userName);
	}

	/**
	 * Method to remove the employee permission record from the DB
	 *
	 * @return private Boolean removeById(Long id) { try {
	 *         permissionDetailsRepo.deleteById(id); return true; } catch (Exception
	 *         e) { log.error("Exception occurred while removing permission
	 *         details"); return false; } }
	 */

	@Override
	public EmployeePermissionStats updatePermissionStatistics(EmployeePermissionDetails permissionDetails) {

		UserDetails userDetails = permissionDetails.getUserDetails();

		EmployeePermissionStats permissionStats = this.findByUserDetailsAndMonth(userDetails,
				permissionDetails.getMonth());

		Long permissionTime = utility.hoursToMinutes(permissionDetails.getStartTime(), permissionDetails.getEndTime());

		// If the hoursTaken is equal to 3 or less than 3
		if ((permissionStats.getHoursTaken().getHour() == 3) || permissionStats.getHoursTaken().getHour() < 3) {
			// minus permissionTime from hoursTaken
			LocalTime updatedHourTaken = permissionStats.getHoursTaken().minusMinutes(permissionTime);
			// minus hoursTaken from Total permission for remaining permissionTime
			LocalTime updatedRemainingPermissionTime = permissionStats.getTotalPermission()
					.minusMinutes(utility.convertTimeToMinutes(updatedHourTaken));
			if (permissionStats.getRemainingPermission().getHour() == permissionStats.getTotalPermission().getHour()) {
				updatedHourTaken = LocalTime.of(0, 0);
				updatedRemainingPermissionTime = LocalTime.of(0, 0);
			}
			permissionStats.setHoursTaken(updatedHourTaken).setRemainingPermission(updatedRemainingPermissionTime)
					.setOverduePermission(LocalTime.of(0, 0));
			this.save(permissionStats);
			return permissionStats;
			// Else if hoursTaken is greater than 3
		} else {
			long positiveTimeDifference = permissionTime;
			LocalTime updatedHoursTaken = permissionStats.getHoursTaken().minusMinutes(positiveTimeDifference);
			LocalTime updatedRemainingTime = permissionStats.getRemainingPermission();
			LocalTime updatedOverdueTime = permissionStats.getOverduePermission();

			if (updatedHoursTaken.isAfter(LocalTime.of(3, 0))) {
				updatedOverdueTime = updatedOverdueTime.minusMinutes(positiveTimeDifference);
			}

			if (updatedHoursTaken.isBefore(LocalTime.of(3, 0))) {
				updatedOverdueTime = LocalTime.of(0, 0);
				updatedRemainingTime = permissionStats.getTotalPermission().minusHours(updatedHoursTaken.getHour())
						.minusMinutes(updatedHoursTaken.getMinute());
			}

			permissionStats.setHoursTaken(updatedHoursTaken).setRemainingPermission(updatedRemainingTime)
					.setOverduePermission(updatedOverdueTime);
			this.save(permissionStats);
			return permissionStats;
		}
	}

	/**
	 * Method to update permission stats in when edit permission api invokes from
	 * user
	 *
	 * @param permissionDto
	 * @return
	 */
	@Override
	public EmployeePermissionStats updateEditPermissionStatistics(RequestPermissionDto permissionDto,
			long timeDifference, EmployeePermissionStats employeePermissionStats) {
		LocalTime totalPermissionLimit = LocalTime.of(3, 0);

		// Extending permission time
		if (timeDifference > 0) {
			LocalTime updatedHoursTaken = employeePermissionStats.getHoursTaken().plusMinutes(timeDifference);
			LocalTime updatedRemainingTime = employeePermissionStats.getRemainingPermission();
			LocalTime updatedOverdueTime = employeePermissionStats.getOverduePermission();

			if (updatedHoursTaken.isAfter(totalPermissionLimit)) {
				updatedRemainingTime = LocalTime.of(0, 0);
				updatedOverdueTime = updatedHoursTaken.minusHours(totalPermissionLimit.getHour())
						.minusMinutes(totalPermissionLimit.getMinute());
			} else {
				updatedRemainingTime = employeePermissionStats.getTotalPermission()
						.minusHours(updatedHoursTaken.getHour()).minusMinutes(updatedHoursTaken.getMinute());
			}

			employeePermissionStats.setHoursTaken(updatedHoursTaken).setRemainingPermission(updatedRemainingTime)
					.setOverduePermission(updatedOverdueTime);
			this.save(employeePermissionStats);
			return employeePermissionStats;
		}
		// Reducing permission time
		else if (timeDifference < 0) {
			long positiveTimeDifference = Math.abs(timeDifference);
			LocalTime updatedHoursTaken = employeePermissionStats.getHoursTaken().minusMinutes(positiveTimeDifference);
			LocalTime updatedRemainingTime = employeePermissionStats.getRemainingPermission();
			LocalTime updatedOverdueTime = employeePermissionStats.getOverduePermission();

			if (updatedHoursTaken.isAfter(LocalTime.of(3, 0))) {
				updatedOverdueTime = updatedOverdueTime.minusMinutes(positiveTimeDifference);
			}

			if (updatedHoursTaken.isBefore(LocalTime.of(3, 0))) {
				updatedOverdueTime = LocalTime.of(0, 0);
				updatedRemainingTime = employeePermissionStats.getTotalPermission()
						.minusHours(updatedHoursTaken.getHour()).minusMinutes(updatedHoursTaken.getMinute());
			}

			employeePermissionStats.setHoursTaken(updatedHoursTaken).setRemainingPermission(updatedRemainingTime)
					.setOverduePermission(updatedOverdueTime);
			this.save(employeePermissionStats);
			return employeePermissionStats;
		} else {
			return employeePermissionStats;
		}
	}

	/**
	 *
	 * Method to fetch the employee's details and permission
	 *
	 * @param existsEmployeeData
	 * @param id
	 * @return
	 */
	@Override
	public EmployeePermissionDetails findPermissionDetailsByUser(UserDetails existsEmployeeData, long id) {
		return permissionDetailsRepo.findByIdAndUserDetails(id, existsEmployeeData)
				.orElseThrow(() -> new CustomExceptionDto("Permission not found with this employee"));
	}

	@Override
	public EmployeeWfhDetails findWorkFromHomeDetailsByUser(UserDetails existsEmployeeData, Integer id) {
		return employeeWfhDetailRepo.findByIdAndUserDetails(id, existsEmployeeData)
				.orElseThrow(() -> new CustomExceptionDto("Permission not found with this employee"));
	}

	@Override
	public List<EmployeeWfhDetails> fetchWorkFromHomeDetails(Integer employeeId) {
		return employeeWfhDetailRepo.findWorkFromHomeDetailsByEmpId(employeeId);
	}

	@Override
	public List<WfhProjection> findAllWorkFromHomeDetailsByEmpIds(List<?> employeeIdsProjections) {
		return employeeWfhDetailRepo.findAllWorkFromHomeDetailsByEmpIds(employeeIdsProjections);
	}

	@Override
	public WorkFromHomeProjectionStats findOverDueAndDaysTakenByUserDetailsAndMonth(String getuserName, String month) {
		return employeeWfhDetailRepo.findOverDueAndDaysTakenByUserDetailsAndMonth(getuserName, month);
	}

	@Override
//	public EmployeeWfhStats updateWfhStatistics(@Valid WfhRequestDto requestDto, long dateDifference,
//			EmployeeWfhStats employeeWfhStats) {
//		int totalWfhLimit = 4; 
//
//		   double updatedDaysTaken = employeeWfhStats.getDaysTaken() + dateDifference;
//	        double updatedRemainingDays = employeeWfhStats.getRemainingWfhDays();
//	        double updatedOverdueDays = employeeWfhStats.getOverdueWfhDays();
//		if(dateDifference>0)
//		{
//		        if(updatedDaysTaken>totalWfhLimit)
//		        {
//		        	updatedRemainingDays = 0;
//		            updatedOverdueDays = updatedDaysTaken - totalWfhLimit;
//		        }
//		        else {
//		            updatedRemainingDays = employeeWfhStats.getTotalWfhDays() - updatedDaysTaken;
//		        }
//		        employeeWfhStats.setDaysTaken(updatedDaysTaken)
//                .setRemainingWfhDays(updatedRemainingDays)
//                .setOverdueWfhDays(updatedOverdueDays);
//		        daoService.save(employeeWfhStats);
////		        this.save(employeeWfhStats);
//		        return employeeWfhStats;
//		}
//		else if(dateDifference<0)
//		{
//			long positiveDateDifference = Math.abs(dateDifference);
//			double updatedDaysTaken = employeeWfhStats.getDaysTaken() - (long) positiveDateDifference;
//	        double updatedRemainingDays = employeeWfhStats.getRemainingWfhDays();
//	        double updatedOverdueDays = employeeWfhStats.getOverdueWfhDays();
//	        if (updatedDaysTaken > totalWfhLimit) {
//	            updatedOverdueDays -= positiveDateDifference;
//	        }
//	        if (updatedDaysTaken <= totalWfhLimit) {
//	            updatedOverdueDays = 0;
//	            updatedRemainingDays = employeeWfhStats.getTotalWfhDays()- updatedDaysTaken;
//	        }
//	        employeeWfhStats.setDaysTaken(updatedDaysTaken)
//            .setRemainingWfhDays(updatedRemainingDays)
//            .setOverdueWfhDays(updatedOverdueDays);
//	        daoService.save(employeeWfhStats);
////	        	this.save(employeeWfhStats);
//	        	 return employeeWfhStats;
//		}
//		return employeeWfhStats;
//	}

	public EmployeeWfhStats updateWfhStatistics(@Valid WfhRequestDto requestDto, long dateDifference,
			EmployeeWfhStats employeeWfhStats) {
		int totalWfhLimit = 4;

		double updatedDaysTaken = employeeWfhStats.getDaysTaken();
		double updatedRemainingDays = employeeWfhStats.getRemainingWfhDays();
		double updatedOverdueDays = employeeWfhStats.getOverdueWfhDays();

		if (dateDifference > 0) {
			updatedDaysTaken += dateDifference;
			if (updatedDaysTaken > totalWfhLimit) {
				updatedRemainingDays = 0;
				updatedOverdueDays = updatedDaysTaken - totalWfhLimit;
			} else {
				updatedRemainingDays = totalWfhLimit - updatedDaysTaken;
			}
		} else if (dateDifference < 0) {
			double positiveDateDifference = Math.abs(dateDifference);
			updatedDaysTaken -= positiveDateDifference;
			if (updatedDaysTaken > totalWfhLimit) {
				updatedOverdueDays -= positiveDateDifference;
			} else {
				updatedOverdueDays = 0;
				updatedRemainingDays = totalWfhLimit - updatedDaysTaken;
			}
		}

		employeeWfhStats.setDaysTaken(updatedDaysTaken).setRemainingWfhDays(updatedRemainingDays)
				.setOverdueWfhDays(updatedOverdueDays);

		return daoService.save(employeeWfhStats);
	}

	@Override
	public List<String> fetchDeviceTokenByDept(String department, List<String> roles) {
		log.info("dept: {}", department);
		log.info("roles: {}", roles);
		return userDetailsRepository.fetchDeviceTokenByDept(department, roles);
	}

	@Override
	public String fetchDeviceTokenByEmployee(String department, String role) {
		return userDetailsRepository.fetchDeviceTokenByEmployee(department, role);
	}

	@Override
	public List<String> fetchMgmtDeviceToken() {
		return userDetailsRepository.fetchDeviceTokenMngmt();
	}

	@Override
	public EmployeeWfhDetails findWfhDetailsById(Long id) {
		return employeeWfhDetailRepo.findWfhDetailsById(id);
	}

	@Override
	public List<EmployeeWfhDetails> findAllEmployeeByStatus(Ems2p0Status status) {
		return employeeWfhDetailRepo.findAllByStatus(Ems2p0Status.PENDING);
	}

	@Override
	public EmployeeWfhStats updateEditPermissionStatistics(WfhRequestDto wfhRequestDto,
			EmployeeWfhStats employeeWfhStats) {
		double updatedDaysTaken = employeeWfhStats.getDaysTaken();

		employeeWfhStats.setDaysTaken(updatedDaysTaken);
		this.save(employeeWfhStats);
		return employeeWfhStats; 
	}

	@Override
	public void deleteAllWfhDetails(String username) {
		employeeWfhDetailRepo.deleteAll();
		log.info("WFH Details table cleared at and cleared by :{} :{} ", LocalDateTime.now(),username);
	}

	@Override
	public void deleteAllWfhStats(String username) {
		employeeWfhStatsRepo.deleteAll();
		log.info("WFH Stats table cleared at and cleared by :{} :{}", LocalDateTime.now(),username);
	}

}
