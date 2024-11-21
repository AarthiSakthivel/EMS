package com.ems2p0.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.ems2p0.model.EmployeeRoleManagement;

@EnableJpaRepositories
public interface EmployeeRoleManagementRepository extends JpaRepository<EmployeeRoleManagement, Long> {

    @Query(value = "select * from emp_role_mgmt where emp_mgmt_empid = :empId ", nativeQuery = true)
    EmployeeRoleManagement findByEmp(@Param("empId") Long empId); //@Param("empId") BigInteger empId

}
