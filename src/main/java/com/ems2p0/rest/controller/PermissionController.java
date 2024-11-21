package com.ems2p0.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.response.EmployeeDetailsDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.PermissionDetailsResponseDto;
import com.ems2p0.dto.response.PermissionStatsResponseDto;
import com.ems2p0.service.PermissionService;
import com.ems2p0.utils.Ems2p0Constants;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * EMS 2.0 - Employee permission controller layer where we're manipulating all
 * the permission APIS such as CRUD operations with DB based on the authorities
 * by spring security.
 *
 * @author Mohan
 * @version v1.0.0
 * @category Permissions functionality
 * @apiNote - Developer should be responsible to create the permission related
 *          APIS by using this layer.
 */
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Validated
public class PermissionController {

	/**
	 * Injected employee permission service layer
	 */
	private final PermissionService permissionService;

	/**
	 * Api method to request / create the permission by using date and time with
	 * specified reason.
	 *
	 * @param permissionDto
	 * @return {@link - GenericResponseDto<PermissionDetailsResponseDto>}
	 * @author Mohan
	 * @Secured - Should be authorized as employee or reporting manager role
	 */
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@PostMapping("/request")
	public ResponseEntity<GenericResponseDto<PermissionDetailsResponseDto>> requestPermission(
			@RequestBody @Valid RequestPermissionDto permissionDto) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.requestPermission(permissionDto));
	}

	/**
	 * Api method to edit the requested or created permission by the employee or
	 * reporting manager.
	 *
	 * @param permissionDto
	 * @return {@link - GenericResponseDto<PermissionDetailsResponseDto> }
	 * @author Aarthi Shakthivel
	 * @Secured - Should be authorized as employee or reporting manager role
	 */
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@PutMapping("/edit")
	public ResponseEntity<GenericResponseDto<PermissionDetailsResponseDto>> editPermission(
			@RequestBody @Valid RequestPermissionDto permissionDto) throws Exception {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(permissionService.editPermission(permissionDto));
	}

	/**
	 * Api method to fetch the permission statistics details of employees
	 *

	 * @return {@link - GenericResponseDto<PermissionStatsResponseDto>} 

	 * @return {@link - GenericResponseDto<List<PermissionStatsResponseDto>>}
	 * @author Mohan
	 * @Secured - Should be authorized as employee or reporting manager role
	 */
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@GetMapping("/stats")
	public ResponseEntity<GenericResponseDto<PermissionStatsResponseDto>> fetchPermissionStatsByUsername(
			@RequestParam(required = false) String month) {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchPermissionStatsByUsername(month));
	}

	/**
	 * Api method to fetch the permission details of all employees
	 *
	 * @return {@link -GenericResponseDto<List<PermissionDetailsResponseDto>> }
	 * @author Mohan
	 * @Secured - Should be authorized as employee or reporting manager or manager
	 */
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@GetMapping("/details")
	public ResponseEntity<GenericResponseDto<List<PermissionDetailsResponseDto>>> fetchPermissionDetailsByUsername() {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchPermissionDetailsByUsername());
	}

	/**
	 * Api method to cancel the requested or created permission of the employee
	 * <p>
	 * * @author Aarthi Shakthivel
	 *
	 * @param request
	 * @return {@link - GenericResponseDto<List<Long>>}
	 * @throws Exception
	 * @Secured - Should be authorized as employee or reporting manager
	 */
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@DeleteMapping("/cancel")
	public ResponseEntity<GenericResponseDto<Long>> cancelPermission(
			@RequestBody @Valid UpdatePermissionStatusRequestDto request) throws Exception {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(permissionService.cancelPermission(request));
	}

	/**
	 * Api method to approve the permission which is requested or created by
	 * employee
	 * <p>
	 * * @author Aarthi Shakthivel
	 *
	 * @param request
	 * @return {@link - GenericResponseDto<List<Long>>}
	 * @throws Exception
	 * @Secured - Should be authorized as manager
	 */
	@RolesAllowed({ Ems2p0Constants.MANAGER })
	@PutMapping("/approve")
	public ResponseEntity<GenericResponseDto<List<Long>>> approvePermission(
			@RequestBody @Valid UpdatePermissionStatusRequestDto request) throws Exception {
	return ResponseEntity.status(HttpStatus.ACCEPTED).body(permissionService.approvePermission(request));
	}

	/**
	 * Api method to reject the requested or created permission of the employee
	 * <p>
	 * * @author Aarthi Shakthivel
	 *
	 * @param request
	 * @return {@link - GenericResponseDto<List<Long>>}
	 * @throws Exception
	 * @Secured - Should be authorized as manager
	 */
	@RolesAllowed({ Ems2p0Constants.MANAGER })
	@DeleteMapping("/reject")
	public ResponseEntity<GenericResponseDto<List<Long>>> rejectPermission(
			@RequestBody @Valid UpdatePermissionStatusRequestDto request) throws Exception {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(permissionService.rejectPermission(request));
	}

	/**
	 * Api method to fetch the employee permission details based on their
	 * departments
	 * <p>
	 * * @author Mohan
	 *
	 * @return {@link - GenericResponseDto<List<PermissionDetailsResponseDto>>}
	 * @Secured - Should be authorized as reporting manager, manager
	 */
	@RolesAllowed({ Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.MANAGER })
	@GetMapping("/employees")
	public ResponseEntity<GenericResponseDto<List<PermissionDetailsResponseDto>>> fetchEmployeePermissionDetails() {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchEmployeePermissionDetails());
	}

	/**
	 * Api method to fetch the all employee's permission details and reports
	 * <p>
	 * * @author Mohan
	 *
	 * @return {@link - GenericResponseDto<List<PermissionDetailsResponseDto>>}
	 * @Secured - Should be authorized a manager, admin
	 */
	@RolesAllowed({ Ems2p0Constants.MANAGER, Ems2p0Constants.ADMIN })
	@GetMapping("/employees/reports")
	public ResponseEntity<GenericResponseDto<List<PermissionDetailsResponseDto>>> fetchAllEmployeePermissionReports() {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchAllEmployeePermissionReports()); 
	}

	/**
	 * Api method to fetch the employee basic details
	 *
	 * @return {@link - GenericResponseDto<List<EmployeeDetailsDto>>}
	 * @author Aarthi Shakthivel
	 * @Secured - Should be authorized a manager, admin
	 */
	@RolesAllowed({ Ems2p0Constants.MANAGER, Ems2p0Constants.ADMIN, Ems2p0Constants.EMPLOYEE })
	@GetMapping("/employee/details")
	public ResponseEntity<GenericResponseDto<List<EmployeeDetailsDto>>> fetchAllEmployeeDetail() {
		return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchEmployeeDetail()); 
	}
}
