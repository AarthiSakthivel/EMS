package com.ems2p0.pushnotification.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NotificationRequest {
private String title;
private String message;
private String topic;
private List<String> token;
}
