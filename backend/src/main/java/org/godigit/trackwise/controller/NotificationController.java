package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmailRequestDTO;
import org.godigit.trackwise.dto.NotificationRequestDTO;
import org.godigit.trackwise.dto.NotificationResponseDTO;
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
    public ResponseEntity<NotificationResponseDTO> createInAppNotification(
            @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO notificationDto = notificationService.createInAppNotification(request);
        return new ResponseEntity<>(notificationDto, HttpStatus.CREATED);
    }

    // Send an email notification
    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequestDTO request) {
        notificationService.sendEmail(request);
        return ResponseEntity.ok().build();
    }
}