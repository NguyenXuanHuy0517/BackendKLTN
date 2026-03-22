package com.project.tenantservice.service;

import com.project.datalayer.entity.Notification;
import com.project.datalayer.repository.NotificationRepository;
import com.project.tenantservice.dto.notification.NotificationDTO;
import com.project.tenantservice.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public List<NotificationDTO> getMyNotifications(Long userId) {
        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public List<NotificationDTO> getUnread(Long userId) {
        return notificationRepository
                .findByUser_UserIdAndIsRead(userId, false).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getUserId().equals(userId)) {
                n.setRead(true);
                n.setReadAt(LocalDateTime.now());
                notificationRepository.save(n);
            }
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUser_UserIdAndIsRead(userId, false);
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    public long countUnread(Long userId) {
        return notificationRepository
                .findByUser_UserIdAndIsRead(userId, false).size();
    }
}