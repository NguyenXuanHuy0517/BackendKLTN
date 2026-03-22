package com.project.tenantservice.dto.notification;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long notificationId;
    private String type;
    private String title;
    private String body;
    private String refType;
    private Long refId;
    private boolean isRead;
    private LocalDateTime createdAt;
}