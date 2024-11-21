package com.ems2p0.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.ems2p0.model.EmployeeManagement;

@EnableJpaRepositories
public interface EmployeeManagementRepo extends JpaRepository<EmployeeManagement, Long> {

    @EntityGraph(attributePaths = {"permissionStats"})
  //  @Query(value="select * FROM emp_mgmt WHERE userName = :userName",nativeQuery = true)
    EmployeeManagement findByUserName(@Param("userName") String userName);

}
