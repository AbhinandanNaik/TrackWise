package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.model.Notification;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.repository.NotificationRepository;
import org.godigit.trackwise.service.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final EmployeeRepository employeeRepository;
  private final JavaMailSender mailSender;

  @Override
  public Notification createInAppNotification(UUID employeeId, String message) {
    Employee e = employeeRepository.findById(employeeId)
      .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
    Notification n = new Notification();
    n.setRecipient(e);
    n.setMessage(message);
    n.setRead(false);
    return notificationRepository.save(n);
  }

  @Override
  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(body);
    mailSender.send(msg);
  }
}
