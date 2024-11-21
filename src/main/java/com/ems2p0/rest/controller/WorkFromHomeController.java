package com.ems2p0.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.request.UpdateWfhStatusRequestDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.WfhDetailsResponseDto;
import com.ems2p0.dto.response.WfhStatsResponseDto;
import com.ems2p0.service.WorkFromHomeService;
import com.ems2p0.utils.Ems2p0Constants;

import jakarta.annotation.security.RolesAllowed;
import jakarta.mail.internet.ParseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * EMS 2.0 - Employee work from home controller layer where we're manipulating all
 * the work from home APIS such as CRUD operations with DB based on the authorities
 * by spring security.
 *
 * @author Aarthi Shakthivel
 * @version v1.0.0
 * @category Work from home functionality
 * @apiNote - Developer should be responsible to create the work form home related
 *          APIS by using this layer.
 */

@RestController
@RequestMapping("/workfromhome")
@RequiredArgsConstructor
@Validated
public class WorkFromHomeController {

	/**
	 * Injected employee work from home service layer
	 */
	
	private final WorkFromHomeService workFromHomeService;

	/**
	 * Api method to request / create the work from home by using date and session with
	 * specified reason.
	 *
	 * @param wfhRequestDto
	 * @return {@link - GenericResponseDto<WfhDetailsResponseDto>}
	 * @author Aarthi Shakthivel
	 * @Secured - Should be authorized as employee or reporting manager role
	 */
	
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@PostMapping("/request")
	public ResponseEntity<GenericResponseDto<WfhDetailsResponseDto>> requestWfh( 
			@RequestBody @Valid WfhRequestDto wfhRequestDto) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(workFromHomeService.requestWfh(wfhRequestDto));
	}
 
	/**
	 * Api method to fetch the work from home statistics details of employees
	 *
	 * @return {@link - GenericResponseDto<List<WfhStatsResponseDto>>} 
	 * @author Aarthi Shakthivel
	 * @Secured - Should be authorized as employee, reporting manager and admin role 
	 */	
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@PutMapping("/edit")
	public ResponseEntity<GenericResponseDto<WfhDetailsResponseDto>> editWfhRequest( 
	        @RequestBody @Valid WfhRequestDto wfhRequestDto) throws Exception {
	    return ResponseEntity.status(HttpStatus.OK).body(workFromHomeService.editWfhRequest( wfhRequestDto));
	}
	
	
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@GetMapping("/stats")
	public ResponseEntity<GenericResponseDto<WfhStatsResponseDto>> fetchWorkFromStatsByUsername(
			@RequestParam(required = false) String month) {
		return ResponseEntity.status(HttpStatus.OK).body(workFromHomeService.fetchWorkFromStatsByUsername(month));
	}
	
	/**
	 * Api method to fetch the work from home details of all employees
	 *
	 * @return {@link -GenericResponseDto<List<WfhDetailsResponseDto>> }
	 * @author Mohan
	 * @Secured - Should be authorized as employee, reporting manager and admin role
	 */
	
	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN })
	@GetMapping("/details")
	public ResponseEntity<GenericResponseDto<List<WfhDetailsResponseDto>>> fetchWorkFromHomeDetailsByUsername() {
		return ResponseEntity.status(HttpStatus.OK).body(workFromHomeService.fetchWorkFromHomeDetailsByUsername()); 
	}

	@RolesAllowed({  Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.MANAGER, Ems2p0Constants.ADMIN })
	@GetMapping("/employees/details")
	public ResponseEntity<GenericResponseDto<List<WfhDetailsResponseDto>>> fetchEmployeesWorkFromHomeDetailsByUsername() {
		return ResponseEntity.status(HttpStatus.OK) 
				.body(workFromHomeService.fetchEmployeesWorkFromHomeDetailsByUsername());   
	} 
	
	/**
	 * Api method to cancel the requested or created work from home   of the employee
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
	public ResponseEntity<GenericResponseDto<Integer>> cancelWfh(@RequestBody @Valid UpdateWfhStatusRequestDto request)
			throws Exception {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(workFromHomeService.cancelWfh(request)); 
	} 
    
	@RolesAllowed({ Ems2p0Constants.MANAGER }) 
	@PutMapping("/approve")
	public ResponseEntity<GenericResponseDto<List<Integer>>> approveWfh(
			@RequestBody @Valid UpdateWfhStatusRequestDto request) throws Exception { 
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(workFromHomeService.approveWfh(request));
	}
	 
	@RolesAllowed({ Ems2p0Constants.MANAGER })
	@DeleteMapping("/reject") 
	public ResponseEntity<GenericResponseDto<List<Integer>>> rejectWfh(
			@RequestBody @Valid UpdateWfhStatusRequestDto request) throws Exception { 
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(workFromHomeService.rejectWfh(request)); 
	}
	
	@DeleteMapping("/clear-details-stats")
	public ResponseEntity<?> clearWfhData() throws Exception { 
		workFromHomeService.clearWfhData();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(""); 
	}
}
