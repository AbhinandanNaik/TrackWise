package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.NotificationResponse;
import org.godigit.trackwise.model.Notification;

public class NotificationMapper {

    public static NotificationResponse toResponseDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationResponse dto = new NotificationResponse();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        if (notification.getRecipient() != null) {
            dto.setRecipientId(notification.getRecipient().getId());
            dto.setRecipientName(
                    notification.getRecipient().getFirstName() + " " + notification.getRecipient().getLastName()
            );
        }

        return dto;
    }
}