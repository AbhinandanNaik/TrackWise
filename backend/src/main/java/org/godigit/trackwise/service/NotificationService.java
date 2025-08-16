package org.godigit.trackwise.service;

import org.godigit.trackwise.model.Notification;

import java.util.UUID;

public interface NotificationService {
  Notification createInAppNotification(UUID userId, String message);
  void sendEmail(String to, String subject, String body);
}
