package com.ems2p0.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.ems2p0.model.EmployeeAttendance;
import com.ems2p0.projections.WorktypeProjections;

import jakarta.transaction.Transactional;

@EnableJpaRepositories
public interface EmployeeAttendanceRepo extends JpaRepository<EmployeeAttendance, Integer> {

	@Query(value = "select wt.workTypeId,wt.work_type from work_type wt ;", nativeQuery = true)
	List<WorktypeProjections> findAllWorktypes();

	EmployeeAttendance findByEmpidAndRegisterid(String empId, Integer last_Id);

	@Query(value = "SELECT * FROM empattendance WHERE name = :userName ORDER BY indate DESC, intime DESC LIMIT 1 ;", nativeQuery = true)
	EmployeeAttendance findByCheckInRecordByNames(@Param("userName") String userName);
	

	 @Transactional 
		@Modifying
		@Query(value = "UPDATE empattendance SET outtime = :currentTime , outdate = :currentDate WHERE outtime IS NULL AND outdate IS NULL\r\n"
				+ "AND intime IS NOT NULL AND indate IS NOT NULL;", nativeQuery=true)
		 void saveAutoLogoutDetails(@Param("currentTime") LocalTime currentTime,@Param ("currentDate") LocalDate currentDate); 

	    
	    @Transactional
		@Modifying
	    @Query(value = "UPDATE emp_permission_details SET permission_status = '0' WHERE permission_status = '2' "
	    		+ "AND date = DATE(NOW());", nativeQuery=true) 
		void saveApproveDetails();


	
	

} 
