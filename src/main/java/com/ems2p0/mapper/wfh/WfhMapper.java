package com.ems2p0.mapper.wfh;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.dto.response.WfhDetailsResponseDto;
import com.ems2p0.dto.response.WfhStatsResponseDto;
import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.EmployeeWfhStats;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.WfhProjection;
import com.ems2p0.projections.WorkFromHomeProjectionStats;
import com.ems2p0.utils.Ems2p0Constants;
import com.ems2p0.utils.WorkFromHomeRequestValidator;

import jakarta.mail.internet.ParseException;
import jakarta.validation.Valid;

@Mapper(componentModel = "spring")
public interface WfhMapper {

	@Mapping(source = "wfhDto.startDate", target = "startDate") 
	@Mapping(source = "wfhDto.endDate", target = "endDate")
	@Mapping(source = "wfhDto.reason", target = "reason")
	@Mapping(source = "wfhDto.requestedSession", target = "requestedSession")
	@Mapping(source = "existsEmployeeData.username", target = "modifiedBy")
	@Mapping(target = "status", expression = "java(mapWfhStatus())")
	@Mapping(target = "createdDateTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "modifiedDateTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(source = "existsEmployeeData.username", target = "createdBy")
	@Mapping(source = "existsEmployeeData", target = "userDetails")
	@Mapping(source = "isOverDue", target = "isOverDue")
	@Mapping(source = "noOfDays", target = "noOfDays")
	
	EmployeeWfhDetails toWfhDto(WfhRequestDto wfhDto, UserDetails existsEmployeeData, boolean isOverDue,
		 double noOfDays) throws ParseException, java.text.ParseException; 

	@Named("mapWfhStatus")
	default Ems2p0Status mapWfhStatus() {
		return Ems2p0Status.PENDING;
	}

	@Mapping(target = "id", source = "saveEmployeeWfhDetails.id")
	@Mapping(target = "month", source = "saveEmployeeWfhDetails.month")
	@Mapping(target = "startDate", source = "saveEmployeeWfhDetails.startDate")
	@Mapping(target = "endDate", source = "saveEmployeeWfhDetails.endDate")
	@Mapping(target = "empId", source = "employee.empSerialNo")
	@Mapping(target = "requestedSession", source = "saveEmployeeWfhDetails.requestedSession")
	@Mapping(target = "userName", source = "employee.empName")
	@Mapping(target = "reason", source = "saveEmployeeWfhDetails.reason")
	@Mapping(target = "status", source = "saveEmployeeWfhDetails.status")
	@Mapping(target = "isOverDue", source = "saveEmployeeWfhDetails.isOverDue")
	@Mapping(target = "daysTaken", source = "daysTaken")
	@Mapping(target = "noOfDays", source = "saveEmployeeWfhDetails.noOfDays")
	WfhDetailsResponseDto toRequestWfhDto(EmployeeWfhDetails saveEmployeeWfhDetails, EmployeeProjection employee, double daysTaken);
 
	default String getEmpSerialNo(UserDetails userDetails) {
		String empIdStr = String.format(Ems2p0Constants.EMPID_FORMAT, userDetails.getEmpId());
		return Ems2p0Constants.EMP.concat(empIdStr); 
	}

	WfhStatsResponseDto toStatsDto(EmployeeWfhStats employeeWfhStats);

	@Mappings({
		    @Mapping(source = "details.id", target = "id"),
		    @Mapping(source = "details.month", target = "month"),
			@Mapping(source = "details.startDate", target = "startDate"),
			@Mapping(source = "details.endDate", target = "endDate"),
			@Mapping(source = "details.reason", target = "reason"),
			@Mapping(source = "details.requestedSession", target = "requestedSession"),
			@Mapping(source = "details.status", target = "status"),
			@Mapping(source = "employee.empName", target = "userName"),
			@Mapping(source = "details.userDetails.empId", target = "empId"),
			@Mapping(source = "details.isOverDue", target = "isOverDue"),
			@Mapping(source = "daysTaken", target = "daysTaken")})
	WfhDetailsResponseDto toDto(EmployeeWfhDetails details,
			EmployeeProjection  employee, double daysTaken);

	default List<WfhDetailsResponseDto> tolistOfDetails(List<EmployeeWfhDetails> workFromHomeDetails,
			EmployeeProjection employee, EmsDaoService daoService) {
		return workFromHomeDetails.stream().map(details -> {
			WorkFromHomeProjectionStats workFromHomeProjectionStats = daoService
					.findOverDueAndDaysTakenByUserDetailsAndMonth(employee.getUserName(), details.getMonth());
			return toDto(details, employee, workFromHomeProjectionStats.getDaysTaken());
		}).toList(); 
	}

	@Mappings({ @Mapping(source = "projections.id", target = "id"),
			@Mapping(source = "projections.month", target = "month"),
			@Mapping(source = "projections.start_date", target = "startDate"),
			@Mapping(source = "projections.end_date", target = "endDate"),
			@Mapping(source = "projections.wfh_status", target = "status"),
			@Mapping(source = "empSerialNo", target = "empId"),
			@Mapping(source = "empName", target = "userName"),
			@Mapping(source = "projections.wfh_reason", target = "reason"),
           	@Mapping(source = "daysTaken", target = "daysTaken"),
			@Mapping(source = "projections.requested_session", target ="requestedSession"),
			@Mapping(source = "projections.isOverDue", target = "isOverDue"),
			@Mapping(source = "projections.no_of_days", target = "noOfDays")})
	WfhDetailsResponseDto mapProjectionToDto(WfhProjection projections, double daysTaken,
			String empSerialNo, String empName);

	default List<WfhDetailsResponseDto> projectionToListOfDetailsDto(List<WfhProjection> employeeWorkFromHomeDetails,
			EmsDaoService daoService) {
		return employeeWorkFromHomeDetails.stream().map(projections -> {
			UserDetails user = daoService.loadUserByUsername(projections.getuserName());
//			String empSerialNo = this.getEmpSerialNo(user); 
			EmployeeProjection employeeProjection = daoService.loadEmployeeByUsername(user.getUsername());
			WorkFromHomeProjectionStats statsProjection = daoService
					.findOverDueAndDaysTakenByUserDetailsAndMonth(projections.getuserName(), projections.getmonth());
			return this.mapProjectionToDto(projections, statsProjection.getDaysTaken(), 
					employeeProjection.getEmpSerialNo(), employeeProjection.getEmpName()); 
		}).collect(Collectors.toList());       
	} 

	@Mapping(target = "id", source = "employeeWfhDetails.id")
	@Mapping(target = "month", source = "employeeWfhDetails.month")
	@Mapping(target = "startDate", source = "employeeWfhDetails.startDate")
	@Mapping(target = "endDate", source = "employeeWfhDetails.endDate")
	@Mapping(target = "status", source = "employeeWfhDetails.status")
	@Mapping(target = "empId", source = "empSerialNo")
	@Mapping(target = "userName", source = "empName")
	@Mapping(target = "reason", source = "employeeWfhDetails.reason")
	@Mapping(target = "daysTaken", source = "daysTaken")
	@Mapping(target = "isOverDue", source = "employeeWfhDetails.isOverDue") 
	@Mapping(target = "noOfDays" , source = "employeeWfhDetails.noOfDays")
	WfhDetailsResponseDto toWfhDetailDto(EmployeeWfhDetails employeeWfhDetails, String empSerialNo, String empName, 
			double daysTaken);

	default List<WfhDetailsResponseDto> toListOfWfhDetailsDto(List<EmployeeWfhDetails> employeeWfhDetailsList,
			EmsDaoService daoService) {
		//double overDueDaysTaken = 0;
		return employeeWfhDetailsList.stream().map(employeeWfhDetails -> {
			String empSerialNo = this.getEmpSerialNo(employeeWfhDetails.getUserDetails()); 
			WorkFromHomeProjectionStats WfhstatsProjection = daoService 
					.findOverDueAndDaysTakenByUserDetailsAndMonthForWfh(
							employeeWfhDetails.getUserDetails().getUsername(), employeeWfhDetails.getMonth());
			String empName = daoService.loadEmployeeByUsername(employeeWfhDetails.getUserDetails().getUsername())
					.getEmpName();
			return toWfhDetailDto(employeeWfhDetails, empSerialNo, empName, WfhstatsProjection.getDaysTaken()
				); 
		}).toList();  
	}

	
	@Mapping(target = "id", ignore = true) 
    @Mapping(target = "status", constant = "PENDING") 
    @Mapping(source = "requestDto.startDate", target = "startDate")
    @Mapping(source = "requestDto.endDate", target = "endDate")
    @Mapping(source = "requestDto.reason", target = "reason")
    @Mapping(source = "requestDto.month", target = "month")
    @Mapping(source = "isOverDue", target = "isOverDue")
	@Mapping(source = "requestDto.requestedSession", target = "requestedSession")
    @Mapping(source = "existsEmployeeData.username", target = "createdBy")
    @Mapping(source = "existsEmployeeData.username", target = "modifiedBy")
	EmployeeWfhDetails toUpdateDoto(@Valid WfhRequestDto requestDto, long dateDifference,
			EmployeeWfhDetails existEmployeeWorkFromHomeDetails, UserDetails existsEmployeeData, Boolean isOverDue) throws ParseException, java.text.ParseException;


	@Mapping(source = "existingWfhDetails.id", target = "id")
	@Mapping(target = "existingWfhDetails.status", constant = "PENDING")
	@Mapping(source = "wfhRequestDto.startDate", target = "startDate")
	@Mapping(source = "wfhRequestDto.endDate", target = "endDate")
	@Mapping(source = "wfhRequestDto.reason", target = "reason")
	@Mapping(source = "wfhRequestDto.month", target = "month")
	@Mapping(source = "isOverDue", target = "isOverDue")
	@Mapping(source = "newUpdatedNoOfDays", target = "noOfDays")
	@Mapping(source = "wfhRequestDto.requestedSession", target = "requestedSession")
	@Mapping(source = "existsEmployeeData.username", target = "createdBy")
	@Mapping(source = "existsEmployeeData.username", target = "modifiedBy")
	EmployeeWfhDetails updateWfhDto(EmployeeWfhDetails existingWfhDetails, UserDetails existsEmployeeData, WfhRequestDto wfhRequestDto, boolean isOverDue,
									double newUpdatedNoOfDays) throws ParseException, java.text.ParseException;
	
	
	@BeforeMapping
	default void validateWfh(WfhRequestDto wfhRequestDto) throws ParseException, java.text.ParseException {
		WorkFromHomeRequestValidator.validateDateAndYear(wfhRequestDto);
	}

//	@Mapping(source = "existingWfhDetails.id", target = "id") 
//    @Mapping(target = "existingWfhDetails.status", constant = "PENDING") 
//    @Mapping(source = "wfhRequestDto.startDate", target = "startDate")
//    @Mapping(source = "wfhRequestDto.endDate", target = "endDate")
//    @Mapping(source = "wfhRequestDto.reason", target = "reason")
//    @Mapping(source = "wfhRequestDto.month", target = "month")
//    @Mapping(source = "isOverDue", target = "isOverDue")
//	@Mapping(source = "wfhRequestDto.requestedSession", target = "requestedSession")
//    @Mapping(source = "existsEmployeeData.username", target = "createdBy")
//    @Mapping(source = "existsEmployeeData.username", target = "modifiedBy")
//	EmployeeWfhDetails updateWfhDto(EmployeeWfhDetails existingWfhDetails, UserDetails existsEmployeeData, WfhRequestDto wfhRequestDto, boolean isOverDue) throws ParseException, java.text.ParseException;

}
