package com.ems2p0.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.model.EmployeePermissionDetails;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.PermissionProjections;

@EnableJpaRepositories
public interface EmployeePermissionDetailsRepo extends JpaRepository<EmployeePermissionDetails, Long> {



	@Query(value = """
            SELECT 
                epd.id, 
                epd.month, 
                epd.date, 
                epd.permission_status, 
                epd.permission_reason,
                epd.start_time, 
                epd.end_time, 
                epd.created_by, 
                epd.created_date_time,
                epd.modified_by, 
                epd.modified_date_time, 
                epd.is_over_due, 
                epd.userCredentialEmp_id
            FROM 
                emp_permission_details epd
            WHERE 
                epd.userCredentialEmp_id = :empId
                AND epd.created_date_time >= DATE_SUB(NOW(), INTERVAL 6 MONTH) 
            ORDER BY 
                epd.created_date_time DESC;
        """, nativeQuery = true)
	List<EmployeePermissionDetails> findPermissionDetByEmployee(@Param("empId") Integer empId);
	@Query(value = """
		    SELECT
		        ep.id, 
		        ep.month, 
		        ep.date, 
		        ep.start_time, 
		        ep.end_time, 
		        ep.permission_reason,
		        ep.permission_status, 
		        em.empSerialNo, 
		        em.userName
		    FROM 
		        emp_permission_details ep
		    INNER JOIN 
		        emp_mgmt em
		    ON
		        ep.userCredentialEmp_id = em.empId
		    WHERE 
		        em.empId IN (:employeeIds)
		    ORDER BY 
		        ep.created_date_time DESC
		    """, nativeQuery = true)

	List<PermissionProjections> findAllByEmpIds(@Param("employeeIds") List<?> employeeIds);       

	@Query(value = """
            SELECT
                 epd.id, 
                 epd.month, 
                 epd.date, 
                 epd.permission_status, 
                 epd.permission_reason,
                 epd.start_time, 
                 epd.end_time, 
                 epd.is_over_due, 
                 epd.created_by, 
                 epd.created_date_time,
                 epd.modified_by,  
                 epd.modified_date_time, 
                 epd.userCredentialEmp_id
            FROM 
                 emp_permission_details epd
            WHERE 
                 epd.created_date_time >= DATE_SUB(NOW(), INTERVAL 2 MONTH)
            ORDER BY 
                 epd.created_date_time DESC
            """, nativeQuery = true)

	List<EmployeePermissionDetails> findAllEmployeesByPermissionDetails();

	@Query(value = "select em.empSerialNo, em.empName from emp_mgmt em", nativeQuery = true)
	List<EmployeeProjection> findAllEmpNameAndId();

	EmployeePermissionDetails findByUserDetailsAndStartTimeAndDateAndStatus(UserDetails existEmployee,
			LocalTime localTime, LocalDate date, Ems2p0Status status);

    Optional<EmployeePermissionDetails> findByIdAndUserDetails(long id,UserDetails existsEmployeeData);
}
