package com.project.hostservice.mapper;

import com.project.datalayer.entity.Notification;
import com.project.hostservice.dto.notification.NotificationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationResponseDTO(
                notification.getNotificationId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getRefType(),
                notification.getRefId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}


