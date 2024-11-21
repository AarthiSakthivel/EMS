package com.ems2p0.service;

import java.util.List;

import com.ems2p0.dto.exception.CheckInProcessingException;
import com.ems2p0.dto.exception.CheckOutProcessingException;
import com.ems2p0.dto.request.AttendanceDto;
import com.ems2p0.dto.response.AttendanceResponseDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.projections.WorktypeProjections;

import jakarta.validation.Valid;

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
public interface AttendanceService {

	GenericResponseDto<AttendanceResponseDto> checkIn(@Valid AttendanceDto attendanceDto) throws Exception;

	GenericResponseDto<AttendanceResponseDto> checkOut(@Valid AttendanceDto attendanceDto) throws Exception;

	GenericResponseDto<List<WorktypeProjections>> fetchWorktypes();

	AttendanceResponseDto fetchEmployeeCheckInDetails();

}
