package com.project.tenantservice.service;

import com.project.datalayer.entity.Notification;
import com.project.datalayer.repository.NotificationRepository;
import com.project.tenantservice.dto.notification.NotificationDTO;
import com.project.tenantservice.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến my notification.
 */
@Service
@RequiredArgsConstructor
public class MyNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

        /**
     * Chức năng: Lấy dữ liệu my notifications.
     */
public List<NotificationDTO> getMyNotifications(Long userId) {
        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu unread.
     */
public List<NotificationDTO> getUnread(Long userId) {
        return notificationRepository
                .findByUser_UserIdAndIsRead(userId, false).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark as read.
     */
public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getUserId().equals(userId)) {
                n.setRead(true);
                n.setReadAt(LocalDateTime.now());
                notificationRepository.save(n);
            }
        });
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark all as read.
     */
public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUser_UserIdAndIsRead(userId, false);
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ count unread.
     */
public long countUnread(Long userId) {
        return notificationRepository
                .findByUser_UserIdAndIsRead(userId, false).size();
    }
}
