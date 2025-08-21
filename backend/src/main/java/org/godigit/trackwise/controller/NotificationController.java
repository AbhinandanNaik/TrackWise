package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmailRequest;
import org.godigit.trackwise.dto.NotificationRequest;
import org.godigit.trackwise.dto.NotificationResponse;
import org.godigit.trackwise.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Create an in-app notification for an employee
    @PostMapping("/in-app")
    public ResponseEntity<NotificationResponse> createInAppNotification(
            @RequestBody NotificationRequest request) {
        NotificationResponse notificationDto = notificationService.createInAppNotification(request);
        return new ResponseEntity<>(notificationDto, HttpStatus.CREATED);
    }

    // Send an email notification
    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequest request) {
        notificationService.sendEmail(request);
        return ResponseEntity.ok().build();
    }
}