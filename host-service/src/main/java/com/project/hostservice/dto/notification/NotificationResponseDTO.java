package com.project.hostservice.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long notificationId;
    private String type;
    private String title;
    private String body;
    private String refType;
    private Long refId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}

