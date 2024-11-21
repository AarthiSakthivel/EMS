package com.ems2p0.pushnotification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ems2p0.pushnotification.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Optional<Notification> findByNotifyMessageAndNotifiedByAndReceivedBy(String message, String empName, String join);

}
