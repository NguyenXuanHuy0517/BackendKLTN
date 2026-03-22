package com.project.hostservice.service;

import com.project.datalayer.entity.Notification;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.NotificationRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void sendToUser(Long userId, String type, String title, String body, String refType, Long refId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setRefType(refType);
        notification.setRefId(refId);
        notification.setRead(false);

        notificationRepository.save(notification);
    }
}