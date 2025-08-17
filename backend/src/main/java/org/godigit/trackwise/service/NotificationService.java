package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.EmailRequestDTO;
import org.godigit.trackwise.dto.NotificationRequestDTO;
import org.godigit.trackwise.dto.NotificationResponseDTO;

public interface NotificationService {
  NotificationResponseDTO createInAppNotification(NotificationRequestDTO request);
  void sendEmail(EmailRequestDTO request);
}