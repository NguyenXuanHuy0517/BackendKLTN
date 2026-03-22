package com.project.tenantservice.mapper;

import com.project.datalayer.entity.Notification;
import com.project.tenantservice.dto.notification.NotificationDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(n.getNotificationId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setBody(n.getBody());
        dto.setRefType(n.getRefType());
        dto.setRefId(n.getRefId());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}