package com.project.hostservice.service;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.Notification;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.NotificationRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.notification.NotificationResponseDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final NotificationMapper notificationMapper;

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

    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUser_UserIdAndIsRead(userId, false);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Gửi thông báo đến một tenant cụ thể
     */
    public void sendToTenant(Long tenantId, String type, String title, String body, String refType, Long refId) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tenant: " + tenantId));

        Notification notification = new Notification();
        notification.setUser(tenant);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setRefType(refType);
        notification.setRefId(refId);
        notification.setRead(false);

        notificationRepository.save(notification);
        log.info("Thông báo gửi đến tenant {} - type: {}", tenantId, type);
    }

    /**
     * Gửi thông báo đến tất cả tenants của một host cụ thể
     */
    public void sendToAllTenantsByHost(Long hostId, String type, String title, String body, String refType, Long refId) {
        // Lấy tất cả contracts của host (mỗi contract liên kết host với tenant)
        List<Contract> contracts = contractRepository.findByRoom_Area_Host_UserId(hostId);

        // Lấy tập hợp unique tenants từ contracts
        Set<Long> tenantIds = contracts.stream()
                .map(contract -> contract.getTenant().getUserId())
                .collect(Collectors.toSet());

        log.info("Gửi thông báo đến {} tenants của host {}", tenantIds.size(), hostId);

        // Gửi thông báo cho mỗi tenant
        tenantIds.forEach(tenantId -> {
            try {
                sendToTenant(tenantId, type, title, body, refType, refId);
            } catch (Exception e) {
                log.error("Lỗi gửi thông báo đến tenant {}: {}", tenantId, e.getMessage());
            }
        });
    }

    /**
     * Gửi thông báo đến tenant cụ thể hoặc tất cả tenants của host
     *
     * @param hostId ID của host gửi thông báo
     * @param tenantId ID của tenant nhận thông báo (null nếu gửi đến tất cả)
     * @param type Loại thông báo
     * @param title Tiêu đề
     * @param body Nội dung
     * @param refType Loại tham chiếu
     * @param refId ID tham chiếu
     */
    public void sendNotification(Long hostId, Long tenantId, String type, String title, String body,
                                 String refType, Long refId) {
        if (tenantId != null) {
            // Gửi đến tenant cụ thể
            sendToTenant(tenantId, type, title, body, refType, refId);
        } else {
            // Gửi đến tất cả tenants của host
            sendToAllTenantsByHost(hostId, type, title, body, refType, refId);
        }
    }
}