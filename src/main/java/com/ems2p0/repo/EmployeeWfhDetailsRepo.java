package com.ems2p0.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.WfhProjection;
import com.ems2p0.projections.WorkFromHomeProjectionStats;

import feign.Param;

@EnableJpaRepositories
public interface EmployeeWfhDetailsRepo extends JpaRepository<EmployeeWfhDetails, Integer> {

	Optional<EmployeeWfhDetails> findByIdAndUserDetails(Integer id, UserDetails existsEmployeeData);

	@Query(value = """
			SELECT
			             ewfh.id,
			             ewfh.month,
			             ewfh.start_date,
			             ewfh.end_date,
			             ewfh.no_of_days,
			             ewfh.wfh_status,
			             ewfh.wfh_reason,
			             ewfh.created_by,
			             ewfh.created_date_time,
			             ewfh.modified_by,
			             ewfh.modified_date_time,
			             ewfh.is_over_due,
			             ewfh.requested_session,
			             ewfh.userCredentialEmp_id
			         FROM
			             emp_wfh_details ewfh
			         WHERE
			             ewfh.userCredentialEmp_id = :employeeId
			             AND ewfh.created_date_time >= DATE_SUB(NOW(), INTERVAL 6 MONTH)
			         ORDER BY
			             ewfh.created_date_time DESC;
			""", nativeQuery = true)
	List<EmployeeWfhDetails> findWorkFromHomeDetailsByEmpId(Integer employeeId);

	@Query(value = """
			SELECT
			       ewfh.id,
			       ewfh.month,
			       ewfh.start_date,
			       ewfh.end_date,
			       ewfh.wfh_reason,
			       ewfh.wfh_status,
			       ewfh.no_of_days,
			       ewfh.is_over_due,
			       ewfh.requested_session,
			       em.empSerialNo,
			       em.userName
			   FROM
			       emp_wfh_details ewfh
			   INNER JOIN
			       emp_mgmt em
			   ONBY
			       ewfh.userCredentialEmp_id = em.empId
			   WHERE
			       em.empId IN :employeeIdsProjections
			   ORDER 
			       ewfh.created_date_time DESC
			""", nativeQuery = true)
	List<WfhProjection> findAllWorkFromHomeDetailsByEmpIds(List<?> employeeIdsProjections);

	@Query(value = "SELECT ewfhs.overdueWfhDays,ewfhs.daysTaken,ewfhs.remainingWfhDays FROM emp_wfh_statistics ewfhs JOIN user_credential uc ON \r\n"
			+ "uc.empId = ewfhs.userCredentialEmp_id WHERE uc.userName = :userName AND month= :month ;", nativeQuery = true)
	WorkFromHomeProjectionStats findOverDueAndDaysTakenByUserDetailsAndMonth(String userName, String month);

	@Query(value = """
			SELECT
			ewd.id,
			ewd.requested_session,
			ewd.no_of_days,
			ewd.month,
			ewd.wfh_status,
			ewd.wfh_reason,
			ewd.start_date,
			ewd.end_date,
			ewd.is_over_due,
			ewd.created_by,
			ewd.created_date_time,
			ewd.modified_by,
			ewd.modified_date_time,
			ewd.userCredentialEmp_id
			FROM
			emp_wfh_details ewd
			WHERE
			ewd.created_date_time >= DATE_SUB(NOW(), INTERVAL 2 MONTH)
			ORDER BY
			ewd.created_date_time DESC
			""", nativeQuery = true)
	List<EmployeeWfhDetails> findAllEmployeesByWfhDetails();

	@Query(value = "select * from emp_wfh_details wfh where wfh.id = :id ", nativeQuery = true)
	EmployeeWfhDetails findWfhDetailsById(Long id);

	@Query(value = "select * from emp_wfh_details wfh where wfh.id = :id ", nativeQuery = true)
	EmployeeWfhDetails findWfhDetailsById(Integer id);

	@Query(value = "SELECT * FROM emp_wfh_details e WHERE e.id = :id AND e.start_date >= :monthStart AND e.end_date <= :monthEnd", nativeQuery = true)
	List<EmployeeWfhDetails> findByIdAndDateRange(@Param("id") Integer id, @Param("monthStart") LocalDate monthStart,
			@Param("monthEnd") LocalDate monthEnd);

	List<EmployeeWfhDetails> findAllByStatus(Ems2p0Status pending);

	@Query(value = "SELECT * FROM emp_wfh_details e JOIN user_credential u ON e.userCredentialEmp_id = u.empId WHERE \r\n"
			+ " (e.start_date = :startDate OR e.end_date = :endDate) AND e.wfh_status != 3 \r\n"
			+ "AND u.empId = :empId", nativeQuery = true) 
	EmployeeWfhDetails findByStartDateOrEndDateAndStatusNotAndUserDetails_EmpId(LocalDate startDate, LocalDate endDate,
			 Integer empId);
}
