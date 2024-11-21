package com.ems2p0.client.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ems2p0.repo.EmployeeAttendanceRepo;
import com.ems2p0.utils.Ems2p0Utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Ems2p0 Scheduler - Job scheduler to update the auto logout (checkout) and
 * updating the auto approve for the pending permission by using spring batch
 * 
 * @author Aarthi Shakthivel
 * @category Job Scheduler
 * 
 * @apiNote - Developer should be responsible to use this component for to write
 *          the job and scheduling related functionality in the EMS 2.0
 *          application
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Ems2p0Scheduler {

	/**
	 * Injected utility method to fetch the original date and time by region
	 */
	private final Ems2p0Utility utility;

	/**
	 * Injected attendance repository to update the auto logout
	 */
	private final EmployeeAttendanceRepo employeeAttendancerRepo;

	@Scheduled(cron = "${cron.job.auto.logout.scheduler}") 
	public void JobRunForLogout() throws Exception {
	    log.info("Ems2p0Scheduler : JobRunForLogout() - Start");
		LocalTime currentTime = utility.getOriginalTimeZone().toLocalTime();
		LocalDate currentDate = utility.getOriginalTimeZone().toLocalDate();
		log.info("Ems2p0Scheduler : JobRunForLogout() - CurrentDate : {} ", currentDate);
	    log.info("Ems2p0Scheduler : JobRunForLogout() - CurrentTime : {} ", currentTime);
		employeeAttendancerRepo.saveAutoLogoutDetails(currentTime, currentDate);
		log.info("Job run successfull for auto logout : {} ", currentTime);
	    log.info("Ems2p0Scheduler : JobRunForLogout() - End");

	}

	@Scheduled(cron = "${cron.job.auto.approve.scheduler}")
	public void JobRunForAutoApprove() {
		log.info("Ems2p0Scheduler : JobRunForAutoApprove() - Start");
		employeeAttendancerRepo.saveApproveDetails();
		log.info("Job run successfull for auto approve permission has been completed.");
		log.info("Ems2p0Scheduler : JobRunForAutoApprove() - End");

	}

}
