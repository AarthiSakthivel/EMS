package com.ems2p0.rest.controller;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems2p0.dto.exception.CheckInProcessingException;
import com.ems2p0.dto.exception.CheckOutProcessingException;
import com.ems2p0.dto.request.AttendanceDto;
import com.ems2p0.dto.response.AttendanceResponseDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.projections.WorktypeProjections;
import com.ems2p0.service.AttendanceService;
import com.ems2p0.utils.Ems2p0Constants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * EMS 2.0 - Employee attendance controller layer where we're manipulating all
 * the attendance(checkIn, checkout, work type...) APIS such as CRUD operations
 * with DB based on the authorities by spring security.
 *
 * @author Mohan
 * @version v1.0.0
 * @category Attendance functionality
 * @apiNote - Developer should be responsible to create the attendance related
 * APIS by using this layer.
 */
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Validated
public class AttendanceController {

    /**
     * Injected attendance service to invoke the abstracted methods
     */
    private final AttendanceService attendanceService;

    /**
     * Api to persist the checkIn time and date of the employee
     *
     * @param attendanceDto
     * @return {@link - GenericResponseDto<AttendanceResponseDto>}
     * @throws Exception
     * @authorization - should be authorized as employee , reporting manager
     */
    @RolesAllowed({Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN})
    @PostMapping("/check-in")
    public ResponseEntity<GenericResponseDto<AttendanceResponseDto>> checkIn(
            @RequestBody @Valid AttendanceDto attendanceDto) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkIn(attendanceDto));
    }

    /**
     * Api to persist the check out time and date of the employee
     *
     * @param attendanceDto
     * @return {@link - GenericResponseDto<AttendanceResponseDto>}
     * @throws Exception
     * @authorization - should be authorized as employee , reporting manager
     */
    @RolesAllowed({Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN})
    @PostMapping("/check-out")
    public ResponseEntity<GenericResponseDto<AttendanceResponseDto>> checkOut( 
            @RequestBody @Valid AttendanceDto attendanceDto) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkOut(attendanceDto));
    }

    /**
     * Api to fetch the work type of the organization
     *
     * @return {@link - GenericResponseDto<List<WorktypeProjections>>}
     */
    @PermitAll
    @GetMapping("/employee/work-type")
    public ResponseEntity<GenericResponseDto<List<WorktypeProjections>>> fetchWorktypes() {
        return ResponseEntity.status(HttpStatus.OK).body(attendanceService.fetchWorktypes());
    }

    /**
     * Api to fetch the employee's previous day check-in record by authorization
     *
     * @return
     */
    @RolesAllowed({Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN})
    @GetMapping("/details/check-in")
    public ResponseEntity<GenericResponseDto<AttendanceResponseDto>> fetchEmployeeCheckInDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<AttendanceResponseDto>(true,
                Ems2p0Constants.SUCCESS, attendanceService.fetchEmployeeCheckInDetails()));
    }
}
