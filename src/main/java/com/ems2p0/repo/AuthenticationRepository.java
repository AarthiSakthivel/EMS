package com.ems2p0.repo;



import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.projections.UserProjection;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

/**
 * EMS 2.0 - Repository layer or Data layer which will provide the CREATE , READ
 * , UPDATE , DELETE actions and make the application easy to communicate with
 * the database
 */
@EnableJpaRepositories
public interface AuthenticationRepository extends JpaRepository<UserDetails, Long> {

    @Query(value = "select * from user_credential uc where uc.userName= :username", nativeQuery = true)
    Optional<UserProjection> findByUserName(@Param("username") String username);


    @Query(value = "select em.empId,em.empSerialNo,em.empName,em.userName,em.emailId from emp_mgmt em where em.userName= :username", nativeQuery = true)
    Optional<EmployeeProjection> findEmployeeByUserName(@Param("username") String username);

    @Query(value = "select em.empId,em.empSerialNo,em.userName,em.empName,em.userName,em.emailId from emp_mgmt em where em.emailId= :emailId", nativeQuery = true)
    Optional<EmployeeProjection> findEmployeeByEmailId(@Param("emailId") String emailId);

}
