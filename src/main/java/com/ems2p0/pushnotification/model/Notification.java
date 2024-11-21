package com.ems2p0.pushnotification.model;

import java.time.LocalDateTime;

import com.ems2p0.model.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "notify_id")
	private Long notifyId;
	@Column(name = "notify_message")
	private String notifyMessage;

	@Column(name = "notified_by")
	private String notifiedBy;

	@Column(name = "received_by")
	private String receivedBy;

	@Column(name = "notify_timestamp")
	private LocalDateTime notifyTimestamp;

	@Column(name = "created_date_time")
	private LocalDateTime createdDateTime;

	@Column(name = "updated_date_time")
	private LocalDateTime updatedDateTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empId", nullable = false)
    private UserDetails userDetails;

}
