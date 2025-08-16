package com.company.trackwise.controller;

import com.company.trackwise.model.Notification;
import com.company.trackwise.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  // Create in-app notification for an employee
  @PostMapping("/employee/{employeeId}")
  public ResponseEntity<Notification> createInAppNotification(
    @PathVariable UUID employeeId,
    @RequestParam String message) {
    Notification notification = notificationService.createInAppNotification(employeeId, message);
    return new ResponseEntity<>(notification, HttpStatus.CREATED);
  }

  // Send email to a recipient
  @PostMapping("/email")
  public ResponseEntity<String> sendEmail(
    @RequestParam String to,
    @RequestParam String subject,
    @RequestParam String body) {
    notificationService.sendEmail(to, subject, body);
    return ResponseEntity.ok("Email sent successfully");
  }
}
