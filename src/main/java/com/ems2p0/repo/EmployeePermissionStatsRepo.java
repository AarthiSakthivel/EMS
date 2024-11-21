package com.ems2p0.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.ems2p0.model.EmployeePermissionStats;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.PermissionStatsProjection;
import com.ems2p0.projections.WorkFromHomeProjectionStats;

@EnableJpaRepositories
public interface EmployeePermissionStatsRepo extends JpaRepository<EmployeePermissionStats, Long> {

    Optional<EmployeePermissionStats> findByUserDetailsAndMonth(UserDetails existsEmployeeData, String month);

    List<EmployeePermissionStats> findByUserDetails(UserDetails userDetails);

    @Query(value = "SELECT eps.overduePermission,eps.hoursTaken FROM emp_permission_statistics eps JOIN user_credential uc ON\r\n"
            + "uc.empId = eps.empId WHERE uc.userName = :userName AND month= :month ;", nativeQuery = true) 
    PermissionStatsProjection findOverDueAndHoursTakenByUserDetailsAndMonth(@Param("userName") String userName, @Param("month") String month);
    
    

}
