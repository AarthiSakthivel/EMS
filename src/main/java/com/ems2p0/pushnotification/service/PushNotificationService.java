package com.ems2p0.pushnotification.service;

import com.ems2p0.pushnotification.model.NotificationRequest;
import com.ems2p0.pushnotification.requestdto.UserDetailsRequest;

public interface PushNotificationService {

	boolean saveUserDeviceTokens(UserDetailsRequest request);

	void sendNotification(NotificationRequest request);

}
