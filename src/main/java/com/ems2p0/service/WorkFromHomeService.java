package com.ems2p0.service;

import java.util.List;

import com.ems2p0.dto.request.UpdatePermissionStatusRequestDto;
import com.ems2p0.dto.request.UpdateWfhStatusRequestDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.dto.response.PermissionDetailsResponseDto;
import com.ems2p0.dto.response.WfhDetailsResponseDto;
import com.ems2p0.dto.response.WfhStatsResponseDto;

import jakarta.mail.internet.ParseException;
import jakarta.validation.Valid;

public interface WorkFromHomeService {
	
	GenericResponseDto<WfhDetailsResponseDto>requestWfh(WfhRequestDto wfhRequestDto)
			throws Exception;

	GenericResponseDto<WfhStatsResponseDto> fetchWorkFromStatsByUsername(String month); 
	
	GenericResponseDto<Integer> cancelWfh(UpdateWfhStatusRequestDto request) throws Exception;

	GenericResponseDto<List<WfhDetailsResponseDto>> fetchWorkFromHomeDetailsByUsername();

	GenericResponseDto<List<WfhDetailsResponseDto>> fetchEmployeesWorkFromHomeDetailsByUsername();
	
	GenericResponseDto<WfhDetailsResponseDto> editWfhRequest(@Valid WfhRequestDto wfhRequestDto) throws ParseException, java.text.ParseException;
	
	GenericResponseDto<List<WfhDetailsResponseDto>> fetchAllEmployeeWfhReports();
	
	GenericResponseDto<List<Integer>> approveWfh(UpdateWfhStatusRequestDto request) throws Exception;
	
	GenericResponseDto<List<Integer>> rejectWfh(UpdateWfhStatusRequestDto request) throws Exception;

	void clearWfhData();
	
	

	
}
