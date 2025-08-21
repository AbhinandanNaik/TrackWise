package org.godigit.trackwise.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private UUID recipientId;
    private String recipientName;
    private String message;
    private boolean read;
    private Instant createdAt;
}