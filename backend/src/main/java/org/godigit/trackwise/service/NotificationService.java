package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.EmailRequest;
import org.godigit.trackwise.dto.NotificationRequest;
import org.godigit.trackwise.dto.NotificationResponse;

public interface NotificationService {
  NotificationResponse createInAppNotification(NotificationRequest request);
  void sendEmail(EmailRequest request);
}