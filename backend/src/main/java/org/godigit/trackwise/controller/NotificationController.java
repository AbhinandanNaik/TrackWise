package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmailRequest;
import org.godigit.trackwise.dto.NotificationRequest;
import org.godigit.trackwise.dto.NotificationResponse;
import org.godigit.trackwise.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling all notification-related operations.
 * This includes creating in-app alerts and sending external emails.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "6. Notification Management", description = "Endpoints for creating notifications and sending emails.")
public class NotificationController {

    // The service layer that contains all the business logic for notifications.
    private final NotificationService notificationService;

    /**
     * Creates a new in-app notification for a specific employee.
     * This notification will be stored in the database.
     * Accessible only by ADMIN role.
     * @param request The DTO containing the recipient's ID and the message.
     * @return The created notification's data as a DTO.
     */
    @PostMapping("/in-app")
    @Operation(summary = "Create an in-app notification")
    public ResponseEntity<NotificationResponse> createInAppNotification(
            @RequestBody NotificationRequest request) {
        // Delegate the business logic to the NotificationService.
        NotificationResponse notificationDto = notificationService.createInAppNotification(request);
        // Return a 201 CREATED status to indicate a new resource was created.
        return new ResponseEntity<>(notificationDto, HttpStatus.CREATED);
    }

    /**
     * Sends an email to a specified recipient.
     * This is a fire-and-forget operation.
     * Accessible only by ADMIN role.
     * @param request The DTO containing the recipient, subject, and body of the email.
     * @return An empty 200 OK response to confirm the request was accepted.
     */
    @PostMapping("/email")
    @Operation(summary = "Send an email notification")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequest request) {
        // Delegate the email sending logic to the NotificationService.
        notificationService.sendEmail(request);
        return ResponseEntity.ok().build();
    }
}