package com.ems2p0.mapper.permission;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.response.PermissionDetailsResponseDto;
import com.ems2p0.dto.response.PermissionStatsResponseDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.model.EmployeePermissionDetails;
import com.ems2p0.model.EmployeePermissionStats;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.PermissionProjections;
import com.ems2p0.projections.PermissionStatsProjection;
import com.ems2p0.utils.Ems2p0Constants;
import com.ems2p0.utils.PermissionRequestValidator;

import jakarta.mail.internet.ParseException;

/**
 * EMS 2.0 - Map struct module which responsible to handle all the DTO and
 * entity conversions to reduce the boiler plate code in the application and
 * enhance the best practice among the developer
 *
 * @author Mohan
 * @version v1.0.0
 * @category Permission module - Mapper convert DTO and entity
 * @apiNote - Developer should be responsible to create the interface methods to
 *          convert the DTO or entity by avoiding the boiler plates in the
 *          application
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

	        @Mapping(source = "projection.id", target = "id")
			@Mapping(source = "projection.month", target = "month")
			@Mapping(source = "projection.date", target = "date")
			@Mapping(source = "projection.start_time", target = "startTime")
			@Mapping(source = "projection.end_time", target = "endTime")
			@Mapping(source = "projection.permission_status", target = "status")
			@Mapping(source = "projection.permission_reason", target = "reason")
			@Mapping(source = "projection.empSerialNo", target = "empId")
			@Mapping(source = "empName", target = "userName")
	        @Mapping(source = "hoursTaken", target = "hoursTaken")
			@Mapping(source = "isOverDue", target = "isOverDue") 
	PermissionDetailsResponseDto mapProjectionToDto(PermissionProjections projection, LocalTime hoursTaken,
			String empName, Boolean isOverDue);

	default List<PermissionDetailsResponseDto> projectionToListOfDetailsDto(List<PermissionProjections> projectionList,
			EmsDaoService daoService) {
		return projectionList.parallelStream().map(projections -> {
			PermissionStatsProjection statsProjection = daoService
					.findOverDueAndHoursTakenByUserDetailsAndMonth(projections.getuserName(), projections.getMonth());
			String empName = daoService.loadEmployeeByUsername(projections.getuserName()).getEmpName();
			return this.mapProjectionToDto(projections, statsProjection.gethoursTaken(), empName,
					(ObjectUtils.isNotEmpty(statsProjection.getOverDuePermission())
							&& !(statsProjection.getOverDuePermission().equals(LocalTime.of(0, 0)))));
		}).toList(); 
	}

	        @Mapping(target = "id", source = "saveEmployeePermissionDetails.id") 
			@Mapping(target = "date", source = "saveEmployeePermissionDetails.date")
			@Mapping(target = "month", source = "saveEmployeePermissionDetails.month")
			@Mapping(target = "startTime", source = "saveEmployeePermissionDetails.startTime")
			@Mapping(target = "endTime", source = "saveEmployeePermissionDetails.endTime")
			@Mapping(target = "empId", source = "employee.empSerialNo")
			@Mapping(target = "userName", source = "employee.empName")
			@Mapping(target = "reason", source = "saveEmployeePermissionDetails.reason")
			@Mapping(target = "hoursTaken", source = "hoursTaken")
			@Mapping(target = "isOverDue", source = "saveEmployeePermissionDetails.isOverDue") 
	PermissionDetailsResponseDto toRequestPermissionDto(EmployeePermissionDetails saveEmployeePermissionDetails,
			EmployeeProjection employee, LocalTime hoursTaken);

	        @Mapping(target = "id", source = "employeePermissionDetails.id")
			@Mapping(target = "month", source = "employeePermissionDetails.month")
			@Mapping(target = "date", source = "employeePermissionDetails.date")
			@Mapping(target = "startTime", source = "employeePermissionDetails.startTime")
			@Mapping(target = "endTime", source = "employeePermissionDetails.endTime")
			@Mapping(target = "status", source = "employeePermissionDetails.status")
			@Mapping(target = "empId", source = "employee.empId")
			@Mapping(target = "userName", source = "employee.userName")
			@Mapping(target = "reason", source = "employeePermissionDetails.reason")
			@Mapping(target = "hoursTaken", source = "hoursTaken")
			@Mapping(target = "isOverDue", source = "employeePermissionDetails.isOverDue") 
	PermissionDetailsResponseDto toPermissionDetailsDto(EmployeePermissionDetails employeePermissionDetails,
			EmployeeProjection employee, LocalTime hoursTaken);

	default List<PermissionDetailsResponseDto> toListOfDetailsDto(
			List<EmployeePermissionDetails> employeePermissionDetailsList, EmployeeProjection employee,
			EmsDaoService daoService) {
		return employeePermissionDetailsList.stream().map(employeePermissionDetails -> {
			PermissionStatsProjection statsProjection = daoService.findOverDueAndHoursTakenByUserDetailsAndMonth(
					employee.getUserName(), employeePermissionDetails.getMonth());
			return toPermissionDetailsDto(employeePermissionDetails, employee, statsProjection.gethoursTaken());
		}).toList(); 
	}

	@Mapping(target = "id", source = "employeePermissionDetails.id")
	@Mapping(target = "month", source = "employeePermissionDetails.month")
	@Mapping(target = "date", source = "employeePermissionDetails.date")
	@Mapping(target = "startTime", source = "employeePermissionDetails.startTime")
	@Mapping(target = "endTime", source = "employeePermissionDetails.endTime")
	@Mapping(target = "status", source = "employeePermissionDetails.status")
	@Mapping(target = "empId", source = "empSerialNo")
	@Mapping(target = "userName", source = "empName")
	@Mapping(target = "reason", source = "employeePermissionDetails.reason")
	@Mapping(target = "hoursTaken", source = "hoursTaken")
	@Mapping(target = "isOverDue", source = "isOverDue") 
	PermissionDetailsResponseDto toPermissionDetailDto(EmployeePermissionDetails employeePermissionDetails,
			String empSerialNo, String empName, LocalTime hoursTaken, Boolean isOverDue);

	default List<PermissionDetailsResponseDto> toListOfPermissionDetailsDto(
			List<EmployeePermissionDetails> employeePermissionDetailsList, EmsDaoService daoService) {
		return employeePermissionDetailsList.stream().map(employeePermissionDetails -> {
			String empSerialNo = this.getEmpSerialNo(employeePermissionDetails.getUserDetails());
			PermissionStatsProjection statsProjection = daoService.findOverDueAndHoursTakenByUserDetailsAndMonth(
					employeePermissionDetails.getUserDetails().getUsername(), employeePermissionDetails.getMonth());
			String empName = daoService.loadEmployeeByUsername(employeePermissionDetails.getUserDetails().getUsername())
					.getEmpName();
			return toPermissionDetailDto(employeePermissionDetails, empSerialNo, empName,
					statsProjection.gethoursTaken(), (ObjectUtils.isNotEmpty(statsProjection.getOverDuePermission())
							&& !(statsProjection.getOverDuePermission().equals(LocalTime.of(0, 0)))));
		}).toList();  
	}

	default String getEmpSerialNo(UserDetails userDetails) {
		String empIdStr = String.format(Ems2p0Constants.EMPID_FORMAT, userDetails.getEmpId());
		return Ems2p0Constants.EMP.concat(empIdStr); 
	}

	PermissionStatsResponseDto toStatsDto(EmployeePermissionStats employeePermissionStats); 

	
			@Mapping(source = "existEmployeePermissionDetails.id", target = "id")
			@Mapping(source = "existEmployeePermissionDetails.month", target = "month")
			@Mapping(source = "existEmployeePermissionDetails.date", target = "date")
			@Mapping(source = "permissionDto.startTime", target = "startTime")
			@Mapping(source = "permissionDto.endTime", target = "endTime")
			@Mapping(source = "existEmployeePermissionDetails.status", target = "status")
			@Mapping(source = "permissionDto.reason", target = "reason")
			@Mapping(source = "isOverDue", target = "isOverDue")
			@Mapping(source = "existsEmployeeData.username", target = "createdBy")
			@Mapping(source = "existsEmployeeData.username", target = "modifiedBy")
			@Mapping(source = "existEmployeePermissionDetails.createdDateTime",target = "createdDateTime")
			@Mapping(target = "modifiedDateTime", expression = "java(java.time.LocalDateTime.now())")
	EmployeePermissionDetails toUpdateDto(RequestPermissionDto permissionDto, EmployeePermissionDetails existEmployeePermissionDetails,
										   UserDetails existsEmployeeData,Boolean isOverDue) throws ParseException, java.text.ParseException;

	default LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

	
			@Mapping(source = "permissionDto.startTime", target = "startTime")
			@Mapping(source = "permissionDto.endTime", target = "endTime")
			@Mapping(source = "permissionDto.reason", target = "reason")
			@Mapping(source = "existsEmployeeData.username", target = "modifiedBy")
			@Mapping(target = "status", expression = "java(mapPermissionStatus())")
			@Mapping(target = "createdDateTime", expression = "java(java.time.LocalDateTime.now())")
			@Mapping(target = "modifiedDateTime", expression = "java(java.time.LocalDateTime.now())")
			@Mapping(source = "existsEmployeeData.username", target = "createdBy")
			@Mapping(source = "isOverDue", target = "isOverDue")
			@Mapping(source = "existsEmployeeData", target = "userDetails")
	EmployeePermissionDetails toDto(RequestPermissionDto permissionDto, UserDetails existsEmployeeData,Boolean isOverDue)
			throws ParseException, java.text.ParseException;

	@Named("mapPermissionStatus")
	default Ems2p0Status mapPermissionStatus() {
		return Ems2p0Status.PENDING;
	}

	@BeforeMapping
	default void validatePermission(RequestPermissionDto permissionDto)
			throws ParseException, java.text.ParseException { 
		PermissionRequestValidator.request(permissionDto); 
	}
}
