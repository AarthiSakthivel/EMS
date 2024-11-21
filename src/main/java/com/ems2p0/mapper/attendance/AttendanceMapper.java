package com.ems2p0.mapper.attendance;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ems2p0.dto.response.AttendanceResponseDto;
import com.ems2p0.enums.Worktype;
import com.ems2p0.model.EmployeeAttendance;
import com.ems2p0.utils.Ems2p0Constants;

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
public interface AttendanceMapper {

	        @Mapping(source = "empSerialNo", target = "empId")
			@Mapping(source = "empName", target = "username")
			@Mapping(source = "savedAtttendance.workingIn", target = "worktype")
			@Mapping(source = "savedAtttendance.locationIn", target = "locationIn")
			@Mapping(source = "savedAtttendance.intime", target = "intime")
			@Mapping(source = "savedAtttendance.indate", target = "indate")
			@Mapping(source = "savedAtttendance.outdate", target = "outdate")
			@Mapping(source = "savedAtttendance.outtime", target = "outtime")
			@Mapping(source = "savedAtttendance.registerid", target = "last_Id") 
	AttendanceResponseDto toDto(EmployeeAttendance savedAtttendance, String empSerialNo,String empName);

	@BeforeMapping
	default void mapWorkType(EmployeeAttendance savedAttendance) {
		String workingIn = savedAttendance.getWorkingIn(); 
		switch (workingIn) { 
		case Ems2p0Constants.WORK_TYPE_ONE -> savedAttendance.setWorkingIn(Worktype.WFO.getValue());
		case Ems2p0Constants.WORK_TYPE_TWO -> savedAttendance.setWorkingIn(Worktype.WFH.getValue());
		case Ems2p0Constants.WORK_TYPE_THREE -> savedAttendance.setWorkingIn(Worktype.ON_SITE.getValue());
		
		}
		
	}

}
