package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NotificationRequestDTO {
    private UUID recipientId;
    private String message;
}