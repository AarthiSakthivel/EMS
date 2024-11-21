package com.ems2p0.pushnotification.serviceImpl;

import org.springframework.stereotype.Service;

import com.ems2p0.dao.service.EmsDaoService;
import com.ems2p0.model.UserDetails;
import com.ems2p0.pushnotification.model.NotificationRequest;
import com.ems2p0.pushnotification.requestdto.UserDetailsRequest;
import com.ems2p0.pushnotification.service.FirebaseMessagingService;
import com.ems2p0.pushnotification.service.PushNotificationService;
import com.ems2p0.security.multi_factor.MultifactorAuthenticator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

	private final EmsDaoService daoService;

	private final MultifactorAuthenticator multifactorAuthenticator;

	private final FirebaseMessagingService firebaseMessagingService;

	@Override
	public boolean saveUserDeviceTokens(UserDetailsRequest request) {
		try {
			String userName = multifactorAuthenticator.getLoggedInUserDetail();
			UserDetails user = daoService.loadUserByUsername(userName);
			user.setEmpDeviceToken(request.getEmpDeviceToken());
			daoService.save(user);
			return true;
		} catch (Exception e) {
			log.error("Exception occured while saving the device token");
			return false;
		}
	}

	@Override
	public void sendNotification(NotificationRequest request) {
		try {
			firebaseMessagingService.sendMessageToToken(request);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

}
