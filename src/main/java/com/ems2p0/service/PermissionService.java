package com.ems2p0.service;

import java.util.List;

import com.ems2p0.dto.exception.UpdateExceptionMsg;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.response.EmployeeDetailsDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.PermissionDetailsResponseDto;
import com.ems2p0.dto.response.PermissionStatsResponseDto;

/**
 * EMS 2.0 - Interface layer to maintain all api methods and functionalities to
 * hide their business logic and represent the low level visibility to the
 * controller level
 *
 * @author Mohan
 * @category Permission functionality
 * @Version - v1.0.0
 * @apiNote - Developer should be responsible to declare the abstract method
 *          here and should implement the business logic by the serviceImpl
 *          respectively 
 */
public interface PermissionService { 

	GenericResponseDto<PermissionDetailsResponseDto> requestPermission(RequestPermissionDto permissionDto)
			throws Exception;

	GenericResponseDto<PermissionDetailsResponseDto> editPermission(RequestPermissionDto permissionDto)
			throws Exception;

	GenericResponseDto<Long> cancelPermission(UpdatePermissionStatusRequestDto request) throws Exception; 

	GenericResponseDto<List<Long>> approvePermission(UpdatePermissionStatusRequestDto request) throws Exception;

	GenericResponseDto<List<Long>> rejectPermission(UpdatePermissionStatusRequestDto request) throws Exception;

	GenericResponseDto<PermissionStatsResponseDto> fetchPermissionStatsByUsername(String month);

	GenericResponseDto<List<PermissionDetailsResponseDto>> fetchPermissionDetailsByUsername();

	GenericResponseDto<List<PermissionDetailsResponseDto>> fetchEmployeePermissionDetails();

	GenericResponseDto<List<PermissionDetailsResponseDto>> fetchAllEmployeePermissionReports();

	GenericResponseDto<List<EmployeeDetailsDto>> fetchEmployeeDetail();
}
