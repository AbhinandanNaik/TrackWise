package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmailRequest;
import org.godigit.trackwise.dto.NotificationRequest;
import org.godigit.trackwise.dto.NotificationResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.NotificationMapper;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.model.Notification;
import org.godigit.trackwise.model.User;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.repository.NotificationRepository;
import org.godigit.trackwise.repository.UserRepository;
import org.godigit.trackwise.service.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final EmployeeRepository employeeRepository;
  private final JavaMailSender mailSender;
  UserRepository userRepository;

  @Override
  public NotificationResponse createInAppNotification(NotificationRequest request) {
    Employee recipient = employeeRepository.findById(request.getRecipientId())
            .orElseThrow(() -> new NotFoundException("Employee not found: " + request.getRecipientId()));

    Notification notification = Notification.builder()
            .recipient(recipient)
            .message(request.getMessage())
            .build(); // Defaults for read and createdAt are set in the entity

    Notification saved = notificationRepository.save(notification);
    return NotificationMapper.toResponseDTO(saved);
  }

  @Override
  public void sendEmail(EmailRequest request) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(request.getTo());
    msg.setSubject(request.getSubject());
    msg.setText(request.getBody());
    mailSender.send(msg);
  }

  @Override
  public void sendNewsAlertToAdmins(String title, String description) {
    List<User> admins = userRepository.findByRole("ROLE_ADMIN");

    for (User admin : admins) {
      // In-app notification
      Notification notification = Notification.builder()
              .recipient(admin.getEmployee()) // still links to Employee entity
              .message("🚨 News Alert: " + title + "\n\n" + description)
              .build();
      notificationRepository.save(notification);

      // Email notification
      if (admin.getEmployee() != null && admin.getEmployee().getEmail() != null) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(admin.getEmployee().getEmail());
        msg.setSubject("🚨 Important IT Asset News Alert");
        msg.setText("Title: " + title + "\n\nDescription:\n" + description);
        mailSender.send(msg);
      }
    }
  }
}