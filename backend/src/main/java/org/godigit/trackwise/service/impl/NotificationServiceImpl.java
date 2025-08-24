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

/**
 * Service implementation for handling all notification-related logic,
 * including creating in-app alerts and sending external emails.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional
public class NotificationServiceImpl implements NotificationService {

  // Dependencies injected by the constructor.
  private final NotificationRepository notificationRepository;
  private final EmployeeRepository employeeRepository;
  private final UserRepository userRepository; // Correctly injected via constructor
  private final JavaMailSender mailSender;

  /**
   * Creates a new in-app notification for a specific employee.
   * @param request The DTO containing the recipient's ID and the message.
   * @return The created notification's data as a DTO.
   */
  @Override
  public NotificationResponse createInAppNotification(NotificationRequest request) {
    // Find the employee who will receive the notification.
    Employee recipient = employeeRepository.findById(request.getRecipientId())
            .orElseThrow(() -> new NotFoundException("Employee not found: " + request.getRecipientId()));

    // Use the builder from the Notification entity to create a new instance.
    Notification notification = Notification.builder()
            .recipient(recipient)
            .message(request.getMessage())
            .build(); // Defaults for 'read' and 'createdAt' are set in the entity itself.

    // Save the new notification to the database.
    Notification saved = notificationRepository.save(notification);

    // Convert the saved entity to a DTO for the API response.
    return NotificationMapper.toResponseDTO(saved);
  }

  /**
   * Sends an email using the configured mail sender.
   * @param request The DTO containing the recipient, subject, and body of the email.
   */
  @Override
  public void sendEmail(EmailRequest request) {
    // Create a simple mail message object.
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(request.getTo());
    msg.setSubject(request.getSubject());
    msg.setText(request.getBody());

    // Delegate the actual sending to Spring's JavaMailSender.
    mailSender.send(msg);
  }

  /**
   * Finds all admin users and sends them both an in-app and email notification.
   * This is used by the NewsScannerJob to alert admins of important news.
   * @param title The title of the news article.
   * @param description The description of the news article.
   */
  @Override
  public void sendNewsAlertToAdmins(String title, String description) {
    // Use the repository to find all users with the "ROLE_ADMIN".
    List<User> admins = userRepository.findByRole("ROLE_ADMIN");

    // Loop through each found admin user.
    for (User admin : admins) {
      // 1. Create and save an in-app notification.
      Notification notification = Notification.builder()
              .recipient(admin.getEmployee()) // The User entity is linked to an Employee profile.
              .message("🚨 News Alert: " + title)
              .build();
      notificationRepository.save(notification);

      // 2. Send an email notification if the admin's employee profile has an email.
      if (admin.getEmployee() != null && admin.getEmployee().getEmail() != null) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(admin.getEmployee().getEmail());
        emailRequest.setSubject("🚨 Important Asset News Alert: " + title);
        emailRequest.setBody("A new, relevant news article was found:\n\n" + description);
        this.sendEmail(emailRequest);
      }
    }
  }
}