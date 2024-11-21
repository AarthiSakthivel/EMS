package com.ems2p0.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.EmployeeWfhStats;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.UserProjection;
import com.ems2p0.projections.WorkFromHomeProjectionStats;

public interface EmployeeWfhStatsRepo extends JpaRepository<EmployeeWfhStats, Long> {

	Optional<EmployeeWfhStats> findByUserDetailsAndMonth(UserDetails existsEmployeeData, String month);

	@Query(value = "SELECT ews.overdueWfhDays,ews.daysTaken FROM emp_wfh_statistics ews JOIN user_credential uc ON\r\n"
            + "uc.empId = ews.userCredentialEmp_id WHERE uc.userName = :userName AND month= :month ;", nativeQuery = true) 
    WorkFromHomeProjectionStats findOverDueAndDaysTakenByUserDetailsAndMonth(@Param("userName") String userName, @Param("month") String month);

}
