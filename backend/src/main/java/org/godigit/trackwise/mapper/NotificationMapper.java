package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.NotificationResponseDTO;
import org.godigit.trackwise.model.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO toResponseDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationResponseDTO dto = new NotificationResponseDTO();
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