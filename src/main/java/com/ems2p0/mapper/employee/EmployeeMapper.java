package com.ems2p0.mapper.employee;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ems2p0.dto.response.EmployeeDetailsDto;
import com.ems2p0.projections.EmployeeProjection;

/**
 * EMS 2.0 - Mapstruct module which responsible to handle all the DTO and entity
 * conversions to reduce the boiler plate code in the application code and
 * enhance the best practice among the developers
 * 
 * @author Mohan
 * @category Employee module - Mapper convert DTO and entity
 * @version v1.0.0
 * 
 * @apiNote - Developer should be responsible to create the interface methods to
 *          convert the DTO or entity by avoiding the boiler plates in the
 *          application
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper {

	default List<EmployeeDetailsDto> tolistOfBasicDetailsDto(List<EmployeeProjection> detailsDtos) {
		return detailsDtos.stream().map(this::toDetailDto).toList();
	}

	        @Mapping(source = "employeeProjection.empSerialNo", target = "empId")
			@Mapping(source = "employeeProjection.empName", target = "empName") 
	EmployeeDetailsDto toDetailDto(EmployeeProjection employeeProjection);

}
