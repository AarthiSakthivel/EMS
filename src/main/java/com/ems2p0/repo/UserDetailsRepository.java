package com.ems2p0.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.ems2p0.model.UserDetails;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {
	Optional<UserDetails> findByUserName(String userName);


	@Query(value = "SELECT uc.empId FROM user_credential uc "
			+ "INNER JOIN emp_role_mgmt erm ON erm.id = uc.employeeRoleManagement_id "
			+ "WHERE erm.dept_name = :departmentName "
			+ "AND (COALESCE(:officialRole, '') = '' OR erm.official_role = :officialRole)", nativeQuery = true)
	List<Integer> fetchByDepartmentNameAndOfficialRole(@Param("departmentName") String departmentName,
			@Param("officialRole") String officialRole);

	@Query(value = "  SELECT uc.empId FROM user_credential uc \r\n"
			+ "INNER JOIN emp_role_mgmt erm ON erm.id = uc.employeeRoleManagement_id ;", nativeQuery = true)
	List<Integer> fetchByDepartmentNameAndOfficialRole();
	
	@Query(value = "SELECT uc.device_token \r\n"
			+ "FROM user_credential uc \r\n"
			+ "INNER JOIN emp_role_mgmt erm \r\n"
			+ "ON erm.id = uc.employeeRoleManagement_id \r\n"
			+ "WHERE erm.dept_name IN (:departmentName,'ALL') \r\n"
			+ "AND uc.is_notification_enable = true\r\n"
			+ "AND erm.official_role IN (:roles);",nativeQuery = true)
	List<String> fetchDeviceTokenByDept(@Param("departmentName") String departmentName, @Param("roles") List<String> roles);

	@Query(value = "SELECT uc.device_token FROM user_credential uc INNER JOIN emp_role_mgmt erm ON erm.id = uc.employeeRoleManagement_id \r\n"
			+ "WHERE uc.is_notification_enable = true AND erm.official_role IN ('ROLE_ADMIN')",nativeQuery = true)
	List<String> fetchDeviceTokenMngmt();
	
	@Query(value = "SELECT uc.device_token \r\n"
			+ "FROM user_credential uc \r\n"
			+ "INNER JOIN emp_role_mgmt erm \r\n"
			+ "ON erm.id = uc.employeeRoleManagement_id \r\n"
			+ "WHERE erm.dept_name = :departmentName \r\n"
			+ "AND (COALESCE(:officialRole, '') = '' OR erm.official_role = :officialRole)\r\n"
			+ "AND uc.is_notification_enable = true",nativeQuery = true)
	String fetchDeviceTokenByEmployee(@Param("departmentName") String departmentName,
			@Param("officialRole") String officialRole);
}
