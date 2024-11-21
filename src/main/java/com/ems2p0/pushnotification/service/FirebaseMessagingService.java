package com.ems2p0.pushnotification.service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.ems2p0.pushnotification.model.NotificationRequest;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseMessagingService {

	public void sendMessageToToken(NotificationRequest request)
			throws InterruptedException, ExecutionException, FirebaseMessagingException {
//		Message message = getPreconfiguredMessageToToken(request);
		MulticastMessage message = this.getPreconfiguredMulticastMessageToTokens(request, request.getToken());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonOutput = gson.toJson(message);
		List<SendResponse> response = sendAndGetResponse(message);
		log.info("Sent message to token. Device token: " + request.getToken() + ", " + response + " msg " + jsonOutput);
	}

	private List<SendResponse> sendAndGetResponse(MulticastMessage message)
			throws InterruptedException, ExecutionException, FirebaseMessagingException {
		return FirebaseMessaging.getInstance().sendMulticast(message).getResponses();
	}

	private AndroidConfig getAndroidConfig(String topic) {
		return AndroidConfig.builder().setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
				.setPriority(AndroidConfig.Priority.HIGH)
				.setNotification(AndroidNotification.builder().setTag(topic).build()).build();
	}

//	private Message getPreconfiguredMessageToToken(NotificationRequest request) {
//		return getPreconfiguredMessageBuilder(request).setToken(request.getToken()).build();
//	}

	private MulticastMessage getPreconfiguredMulticastMessageToTokens(NotificationRequest request,
			List<String> tokens) {
		return MulticastMessage.builder().setAndroidConfig(getAndroidConfig(request.getTopic()))
				.setNotification(
						Notification.builder().setTitle(request.getTitle()).setBody(request.getMessage()).build())
				.addAllTokens(tokens).build();
	}

//	private Message.Builder getPreconfiguredMessageBuilder(NotificationRequest request) {
//		AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
//		Notification notification = Notification.builder().setTitle(request.getTitle()).setBody(request.getMessage())
//				.build();
//		return Message.builder().setAndroidConfig(androidConfig).setNotification(notification);
//	}
//    private AndroidConfig getAndroidConfig(String topic) {
//        return AndroidConfig.builder()
//                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
//                .setPriority(AndroidConfig.Priority.HIGH)
//                .setNotification(AndroidNotification.builder()
//                        .setTag(topic).build()).build();
//    }
    
    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }
   
//    private Message getPreconfiguredMessageToToken(NotificationRequest request) {
//        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
//                .build();
//    }

    private Message.Builder getPreconfiguredMessageBuilder(NotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
//        Message message = Message.builder()
//                .putData("title", "hi")
//                .putData("body", "hello")
//                .build();
//       
       
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody("Check")
                .build();
    return Message.builder().setApnsConfig(apnsConfig)
                    .setAndroidConfig(androidConfig).setNotification(notification);
    }
}
