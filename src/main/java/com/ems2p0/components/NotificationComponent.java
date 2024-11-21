package com.ems2p0.components;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.dto.request.RequestPermissionDto;
import com.ems2p0.dto.request.WfhRequestDto;
import com.ems2p0.enums.OfficialRole;
import com.ems2p0.model.EmployeePermissionDetails;
import com.ems2p0.model.EmployeeWfhDetails;
import com.ems2p0.model.UserDetails;
import com.ems2p0.projections.EmployeeProjection;
import com.ems2p0.pushnotification.model.Notification;
import com.ems2p0.pushnotification.model.NotificationRequest;
import com.ems2p0.pushnotification.repository.NotificationRepository;
import com.ems2p0.pushnotification.service.PushNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EMS 2.0 - Component to handle all of the notification related methods and to
 * invoke the firebase initializer and triggering the notifications
 * 
 * @author Aarthi, Mohan, Vishnu
 * @category Notification operations module Component - component layer
 * @apiNote - Developer should be responsible to each and every API method will
 *          be simple to read and write and should it should be coordinating
 *          with utility methods to reuse the logics by maintaining the high
 *          level code quality by reduce the boiler plates.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationComponent {

	/**
	 * Injected the EMS Data access service layer to invoke the repository
	 * operations
	 */
	private final EmsDaoService daoService;

	/**
	 * Injected push notification service to invoke and trigger the otp to employee
	 */
	private final PushNotificationService notificationService;

	/**
	 * Invoking the push notification topic by configurable type
	 */
	
	 @Autowired
	    private NotificationRepository notificationRepository;
	 
	
	 
	@Value("${ems.push-notification.topic}")
	private String pushNotificationTopic;

	/**
	 * Method to send the OTP notification using devcie token and invoking firebase
	 * methods and logics
	 * 
	 * @param employeeData
	 * @param permissionDto
	 */
	public void sendNotification(UserDetails employeeData, RequestPermissionDto permissionDto, String action) {

		// reporting mngr, mngr, admin

		List<String> rolesToNotify = new ArrayList<>();

		String requesterRole = employeeData.getEmployeeRoleManagement().getOfficialRole().name();

		switch (requesterRole) {
		case "ROLE_EMPLOYEE":
			rolesToNotify.add("ROLE_REPORTING_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			rolesToNotify.add("ROLE_MANAGER");
			break;
		case "ROLE_REPORTING_MANAGER":
			rolesToNotify.add("ROLE_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			break;
		case "ROLE_ADMIN":
			rolesToNotify.add("ROLE_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			break;
		case "ROLE_MANAGER":
			rolesToNotify.add("ROLE__REPORTING_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			rolesToNotify.add("ROLE_EMPLOYEE");
			break;
		default:
			log.warn("Unexpected role: {}", requesterRole);
			return;
		}

		List<String> deviceTokens = daoService
				.fetchDeviceTokenByDept(employeeData.getEmployeeRoleManagement().getDepartmentName(), rolesToNotify);
		log.info("Device tokens to notify the persons : {}", deviceTokens.size());

		List<String> mngmtTokens = daoService.fetchMgmtDeviceToken();
		log.info("Management Device tokens to notify the persons : {}", mngmtTokens.size());

		String message;
		if ("edited".equals(action)) {
			message = new StringBuilder().append("Edited permission request from ").append(permissionDto.startTime())
					.append(" to ").append(permissionDto.endTime()).toString();
		} else {
			message = new StringBuilder().append("Requested permission from ").append(permissionDto.startTime())
					.append(" to ").append(permissionDto.endTime()).toString();
		}
		// requesting emp token
//		String employeeDeviceToken = daoService.fetchDeviceTokenByEmployee(
//				employeeData.getEmployeeRoleManagement().getDepartmentName(), employeeData.getEmpDeviceToken());

		deviceTokens.addAll(mngmtTokens);

		log.info("OverAlldeviceTokens:", deviceTokens);

		deviceTokens.removeIf(StringUtils::isEmpty);

		String title = new StringBuilder().append(employeeData.getEmpName() + " ").append("Permission Request")
				.toString();
		try {
			this.notificationService.sendNotification(new NotificationRequest().setTitle(title).setMessage(message)
					.setToken(deviceTokens).setTopic(pushNotificationTopic));
			log.info("Notification trigger successfully ");
		} catch (Exception e) {
			log.error("Exception occured while sending notification from firebase : {}", e.toString());
		}

		saveNotification(employeeData, rolesToNotify, message);

	}

	private void saveNotification(UserDetails employeeData, List<String> rolesToNotify, String message) {
		Notification notification = new Notification();
		notification.setNotifyMessage(message);
		notification.setNotifiedBy(employeeData.getEmpName());
		notification.setReceivedBy(String.join(",", rolesToNotify));
		notification.setNotifyTimestamp(LocalDateTime.now());
		notification.setCreatedDateTime(LocalDateTime.now());
		notification.setUpdatedDateTime(LocalDateTime.now());
		notification.setUserDetails(employeeData);
		notificationRepository.save(notification);
	}


	public void sendApprovalAndRejectionNotification(EmployeePermissionDetails permissionDetails,
			EmployeeProjection manager, String status) {
		UserDetails employeeData = permissionDetails.getUserDetails();
		OfficialRole employeeRole = employeeData.getEmployeeRoleManagement().getOfficialRole();
		log.info("employeeRole:", employeeRole);
		String departmentName = employeeData.getEmployeeRoleManagement().getDepartmentName();
		log.info("departmentName:", employeeRole);
		String dynamicTitle = status.equalsIgnoreCase("approved") ? "Approved" : "Rejected";
		String title = "Permission " + dynamicTitle;
		String message = String.format(" Permission request from %s to %s has been %s by %s.",
				permissionDetails.getStartTime(), permissionDetails.getEndTime(), dynamicTitle.toLowerCase(),
				manager.getEmpName());
		List<String> rolesToNotify = new ArrayList<>();
		if (employeeRole == OfficialRole.ROLE_REPORTING_MANAGER) {
			// If the permission was put by a reporting manager
			rolesToNotify = Arrays.asList("ROLE_REPORTING_MANAGER", "ROLE_ADMIN");
		} else if (employeeRole == OfficialRole.ROLE_EMPLOYEE) {
			// If the permission was put by an employee
			rolesToNotify = Arrays.asList("ROLE_EMPLOYEE", "ROLE_ADMIN", "ROLE_REPORTING_MANAGER");
		} else if (employeeRole == OfficialRole.ROLE_ADMIN) {
			// If the permission was put by an employee
			rolesToNotify = Arrays.asList("ROLE_ADMIN");
		}
		List<String> deviceTokens = daoService.fetchDeviceTokenByDept(departmentName, rolesToNotify);
		log.info("Device tokens fetched: {}", deviceTokens.size());
		List<String> mngmtTokens = daoService.fetchMgmtDeviceToken();
		log.info("Management device tokens fetched: {}", mngmtTokens.size());
		deviceTokens.addAll(mngmtTokens);
		log.info("Overall device tokens after filtering: {}", deviceTokens.size());
		deviceTokens.removeIf(StringUtils::isEmpty);
		try {
			this.notificationService.sendNotification(new NotificationRequest().setTitle(title).setMessage(message)
					.setToken(deviceTokens).setTopic(pushNotificationTopic));
			log.info("Notification sent successfully: {}", title);
		} catch (Exception e) {
			log.error("Exception occurred while sending notification: {}", e.toString());
		}
		saveApproveRejectionNotification(permissionDetails,manager, rolesToNotify, message);
		
	}

	

	private void saveApproveRejectionNotification(EmployeePermissionDetails permissionDetails,EmployeeProjection manager, List<String> rolesToNotify,
			String message) {
		Notification notification = new Notification();
		notification.setNotifyMessage(message);
		notification.setNotifiedBy(manager.getEmpName());
		notification.setReceivedBy(String.join(",", rolesToNotify));
		notification.setNotifyTimestamp(LocalDateTime.now());
		notification.setCreatedDateTime(LocalDateTime.now());
		notification.setUpdatedDateTime(LocalDateTime.now());
		notification.setUserDetails(permissionDetails.getUserDetails());
		notificationRepository.save(notification);
		
	}

	/**
	**/

	public void sendWfhNotificationForCreateAndEdit(UserDetails employeeData, WfhRequestDto wfhRequestDto,
			String action) {
		// Define roles to notify based on the requester's role
		List<String> rolesToNotify = new ArrayList<>();
		String requesterRole = employeeData.getEmployeeRoleManagement().getOfficialRole().name();
		switch (requesterRole) {
		case "ROLE_EMPLOYEE":
			rolesToNotify.add("ROLE_REPORTING_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			rolesToNotify.add("ROLE_MANAGER");
			break;
		case "ROLE_REPORTING_MANAGER":
			rolesToNotify.add("ROLE_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			break;
		case "ROLE_ADMIN":
			rolesToNotify.add("ROLE_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			break;
		case "ROLE_MANAGER":
			rolesToNotify.add("ROLE_REPORTING_MANAGER");
			rolesToNotify.add("ROLE_ADMIN");
			rolesToNotify.add("ROLE_EMPLOYEE");
			break;
		default:
			log.warn("Unexpected role: {}", requesterRole);
			return;
		}
		// Fetch device tokens for the roles to notify
		List<String> deviceTokens = daoService
				.fetchDeviceTokenByDept(employeeData.getEmployeeRoleManagement().getDepartmentName(), rolesToNotify);
		log.info("deviceTokens", deviceTokens.size());
		List<String> mngmtTokens = daoService.fetchMgmtDeviceToken();
		log.info("Management Device tokens to notify the persons : {}", mngmtTokens.size());
		deviceTokens.addAll(mngmtTokens);
		deviceTokens.removeIf(StringUtils::isEmpty);
		// Construct notification message
		String title = "WFH Request " + action;
		String message = "";
		  if (action.equals("created")) {
		        message = "WFH request from " + wfhRequestDto.startDate() + " to " + wfhRequestDto.endDate()
		                + " has been created by " + employeeData.getEmpName() + "." ;
		    } else if (action.equals("edited")) {
		        message = "WFH request from " + wfhRequestDto.startDate() + " to " + wfhRequestDto.endDate()
		                + " has been edited by " + employeeData.getEmpName() + ". " ;
		    }
		try {
			this.notificationService.sendNotification(new NotificationRequest().setTitle(title).setMessage(message)
					.setToken(deviceTokens).setTopic(pushNotificationTopic));
			log.info("WFH request " + action + " notification sent successfully");
		} catch (Exception e) {
			log.error("Exception occurred while sending WFH request " + action + " notification: {}", e.toString());
		}
		saveNotification(employeeData, rolesToNotify, message);
		
	}

	public void sendApprovalAndRejectionWfhNotification(EmployeeWfhDetails employeeWfhDetails,
			EmployeeProjection manager, String status) {
		UserDetails employeeData = employeeWfhDetails.getUserDetails();
		OfficialRole employeeRole = employeeData.getEmployeeRoleManagement().getOfficialRole();
		log.info("employeeRole:", employeeRole);
		String departmentName = employeeData.getEmployeeRoleManagement().getDepartmentName();
		log.info("departmentName:", departmentName);
		String dynamicTitle = status.equals("approved") ? "Approved" : "Rejected";
		String title = "WFH " + dynamicTitle;
		String message = "WFH request from " + employeeWfhDetails.getStartDate() + " to "
				+ employeeWfhDetails.getEndDate() + " has been " + dynamicTitle.toLowerCase() + " by "
				+ manager.getEmpName() + ".";
		List<String> rolesToNotify = new ArrayList<>();
		if (employeeRole == OfficialRole.ROLE_REPORTING_MANAGER) {
			rolesToNotify = Arrays.asList("ROLE_REPORTING_MANAGER", "ROLE_ADMIN");
		} else if (employeeRole == OfficialRole.ROLE_EMPLOYEE) {
			rolesToNotify = Arrays.asList("ROLE_EMPLOYEE", "ROLE_ADMIN", "ROLE_REPORTING_MANAGER");
		} else if (employeeRole == OfficialRole.ROLE_ADMIN) {
			rolesToNotify = Arrays.asList("ROLE_ADMIN");
		}
		List<String> deviceTokens = daoService
				.fetchDeviceTokenByDept(employeeData.getEmployeeRoleManagement().getDepartmentName(), rolesToNotify);
		List<String> mngmtTokens = daoService.fetchMgmtDeviceToken();
		log.info("Management Device tokens to notify the persons : {}", mngmtTokens.size());
		deviceTokens.addAll(mngmtTokens);
		log.info("DeviceTokens:", deviceTokens);
		deviceTokens.removeIf(StringUtils::isEmpty);
		try {
			this.notificationService.sendNotification(new NotificationRequest().setTitle(title).setMessage(message)
					.setToken(deviceTokens).setTopic(pushNotificationTopic));
			log.info("WFH approval notification sent successfully");
		} catch (Exception e) {
			log.error("Exception occurred while sending WFH approval notification: {}", e.toString());
		}
		saveApproveRejectionWFHNotification(employeeWfhDetails,manager, rolesToNotify, message);
	}

	private void saveApproveRejectionWFHNotification(EmployeeWfhDetails employeeWfhDetails, EmployeeProjection manager,
			List<String> rolesToNotify, String message) {
		Notification notification = new Notification();
		notification.setNotifyMessage(message);
		notification.setNotifiedBy(manager.getEmpName());
		notification.setReceivedBy(String.join(",", rolesToNotify));
		notification.setNotifyTimestamp(LocalDateTime.now());
		notification.setCreatedDateTime(LocalDateTime.now());
		notification.setUpdatedDateTime(LocalDateTime.now());
		notification.setUserDetails(employeeWfhDetails.getUserDetails());
		notificationRepository.save(notification);
	}
}
