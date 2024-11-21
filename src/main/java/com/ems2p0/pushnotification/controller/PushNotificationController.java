package com.ems2p0.pushnotification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems2p0.dto.response.GenericResponseDto;
import com.ems2p0.pushnotification.model.NotificationRequest;
import com.ems2p0.pushnotification.model.PushNotificationResponse;
import com.ems2p0.pushnotification.requestdto.UserDetailsRequest;
import com.ems2p0.pushnotification.service.PushNotificationService;
import com.ems2p0.utils.Ems2p0Constants;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class PushNotificationController {

	private final PushNotificationService pushNotificationService;

	@PostMapping("/send")
	public ResponseEntity<GenericResponseDto<PushNotificationResponse>> pushNotification(@RequestBody NotificationRequest request) {
	    pushNotificationService.sendNotification(request);
	    return ResponseEntity.ok(
	          new GenericResponseDto<>(true, "Notification sent successfully",
	                new PushNotificationResponse(HttpStatus.OK.value(), "Notification sent successfully"))
	    );
	}


	@RolesAllowed({ Ems2p0Constants.EMPLOYEE, Ems2p0Constants.REPORTING_MANAGER, Ems2p0Constants.ADMIN, Ems2p0Constants.MANAGER })
	@PostMapping("/devicetoken")
	public ResponseEntity<GenericResponseDto<String>> saveDeviceTokens(@RequestBody UserDetailsRequest request) {
	    boolean saved = pushNotificationService.saveUserDeviceTokens(request);
	    if (saved) {
	       GenericResponseDto<String> response = new GenericResponseDto<>(true, "Device token saved successfully.", null);
	       return ResponseEntity.ok(response);
	    } else {
	       GenericResponseDto<String> response = new GenericResponseDto<>(false, "Failed to save device token.", null);
	       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
}
