package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Notification;
import org.godigit.trackwise.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Create an in-app notification for an employee
    @PostMapping("/in-app/{employeeId}")
    public ResponseEntity<Notification> createInAppNotification(
            @PathVariable UUID employeeId,
            @RequestParam String message) {
        Notification notification = notificationService.createInAppNotification(employeeId, message);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    // Send an email notification
    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {
        notificationService.sendEmail(to, subject, body);
        return ResponseEntity.ok().build();
    }
}
