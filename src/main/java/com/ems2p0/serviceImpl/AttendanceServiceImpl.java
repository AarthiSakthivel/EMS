package com.ems2p0.serviceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.exception.CheckInProcessingException;
import com.ems2p0.dto.exception.CheckOutProcessingException;
import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.request.AttendanceDto;
import com.ems2p0.dto.response.AttendanceResponseDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.mapper.attendance.AttendanceMapper;
import com.ems2p0.model.EmployeeAttendance;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.WorktypeProjections;
import com.ems2p0.security.multi_factor.MultifactorAuthenticator;
import com.ems2p0.service.AttendanceService;
import com.ems2p0.utils.Ems2p0Constants;
import com.ems2p0.utils.Ems2p0Utility;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 - Interface layer to maintain all api methods and functionalities to
 * hide their business logic and represent the low level visibility to the
 * controller level
 *
 * @author Mohan
 * @category Attendance functionality
 * @Version - v1.0.0
 * @apiNote - Developer should be responsible to declare the abstract method
 *          here and should implement the business logic by the serviceImpl
 *          respectively
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

	/**
	 * Injected Dao service to invoke the DB operation apis
	 */
	private final EmsDaoService daoService;

	/**
	 * Injected attendance mapper to convert into entity to DTO
	 */
	private final AttendanceMapper attendanceMapper;

	/**
	 * Injected Utility component to invoke the generic methods
	 */
	private final Ems2p0Utility utility;

	/**
	 * Injected Multi factor authenticator invoke the OTP methods and logics
	 */
	private final MultifactorAuthenticator multifactorAuthenticator;

	/**
	 * Method to record the checkIn details of the employee
	 *
	 * @throws Exception
	 */
	@Override
	public GenericResponseDto<AttendanceResponseDto> checkIn(@Valid AttendanceDto attendanceDto) throws Exception {
	        String userName = multifactorAuthenticator.getLoggedInUserDetail();
	        EmployeeProjection employee = daoService.loadEmployeeByUsername(userName); 
	        LocalDateTime zoneDateAndTime = utility.getOriginalTimeZone();
	        EmployeeAttendance attendance = new EmployeeAttendance().setEmpid(employee.getEmpSerialNo()).setName(userName)
	                .setIndate(zoneDateAndTime.toLocalDate().toString()).setIntime(zoneDateAndTime.toLocalTime())
	                .setOutdate(null).setOuttime(null).setLocationIn(attendanceDto.locationIn()).setLocationOut("")
	                .setOvertime(null).setPermission(LocalTime.of(0, 0, 0)).setWorkinghours((float) 0)
	                .setTotalworkinghours((float) 0).setWorkingIn(attendanceDto.work_type());
	        EmployeeAttendance savedAttendance = daoService.save(attendance); 
	        return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS, 
	                attendanceMapper.toDto(savedAttendance, employee.getEmpSerialNo(), employee.getEmpName()));
	}
	/**
	 * Method to record the checkout details of the employee
	 *
	 * @throws Exception
	 */
	@Override
	public GenericResponseDto<AttendanceResponseDto> checkOut(@Valid AttendanceDto attendanceDto) throws Exception {
		
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName); 
		EmployeeAttendance fetchCheckIn = daoService.fetchCheckIn(attendanceDto, employee.getEmpSerialNo());
		LocalDateTime zoneDateAndTime = utility.getOriginalTimeZone();
		if (ObjectUtils.isNotEmpty(fetchCheckIn)) { 
			double workingHours = utility.calculateWorkingHours(fetchCheckIn.getIntime(), LocalTime.now());
			double totalWorkingHours = utility.calculateTotalWorkingHours(fetchCheckIn.getIntime(), LocalTime.now());
			EmployeeAttendance saveCheckout = new EmployeeAttendance().setEmpid(fetchCheckIn.getEmpid())
					.setRegisterid(fetchCheckIn.getRegisterid()).setIndate(fetchCheckIn.getIndate())
					.setIntime(fetchCheckIn.getIntime()).setName(userName).setOvertime(fetchCheckIn.getOvertime())
					.setPermission(fetchCheckIn.getPermission()).setOutdate(zoneDateAndTime.toLocalDate().toString())
					.setOuttime(zoneDateAndTime.toLocalTime()).setWorkinghours((float) workingHours)
					.setTotalworkinghours((float) totalWorkingHours).setLocationIn(fetchCheckIn.getLocationIn())
					.setLocationOut(attendanceDto.locationIn()).setWorkingIn(attendanceDto.work_type());
			EmployeeAttendance saveCheckOut = daoService.save(saveCheckout); 
			return new GenericResponseDto<>(true, Ems2p0Constants.SUCCESS,
					attendanceMapper.toDto(saveCheckOut, employee.getEmpSerialNo(), employee.getEmpName()));
		}
		else {
			throw new CustomExceptionDto("CheckIn First to attempt the checkOut");
		}
		}
		
	

	/**
	 * Method to fetch all the work types
	 */
	@Override
	public GenericResponseDto<List<WorktypeProjections>> fetchWorktypes() {
		return new GenericResponseDto<List<WorktypeProjections>>(true, Ems2p0Constants.SUCCESS,
				daoService.fetchWorktypes());
	}

	/**
	 * Method to fetch the employee's last checkIn details by userName
	 */
	@Override 
	public AttendanceResponseDto fetchEmployeeCheckInDetails() {
		String userName = multifactorAuthenticator.getLoggedInUserDetail();
		EmployeeProjection employee = daoService.loadEmployeeByUsername(userName); 
		try {
		EmployeeAttendance employeeAttendance =  daoService.fetchCheckInByUserName(userName);
		return attendanceMapper.toDto(employeeAttendance, employee.getEmpSerialNo(),
				employee.getEmpName());  
		}
		catch (Exception e){
		        AttendanceResponseDto attendanceResponse = new AttendanceResponseDto(null, null, null, null, null, null, null, null, LocalTime.of(23, 0, 0, 0));
			return attendanceResponse;  
		}
	}
}
