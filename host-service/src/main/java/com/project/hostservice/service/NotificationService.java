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

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến notification.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final NotificationMapper notificationMapper;

        /**
     * Chức năng: Gửi to user.
     */
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

        /**
     * Chức năng: Lấy dữ liệu notifications by user.
     */
public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark as read.
     */
public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark all as read.
     */
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
     * Chức năng: Gửi to tenant.
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
     * Chức năng: Gửi to all tenants by host.
     */
public void sendToAllTenantsByHost(Long hostId, String type, String title, String body, String refType, Long refId) {
        
        List<Contract> contracts = contractRepository.findByRoom_Area_Host_UserId(hostId);

        
        Set<Long> tenantIds = contracts.stream()
                .map(contract -> contract.getTenant().getUserId())
                .collect(Collectors.toSet());

        log.info("Gửi thông báo đến {} tenants của host {}", tenantIds.size(), hostId);

        
        tenantIds.forEach(tenantId -> {
            try {
                sendToTenant(tenantId, type, title, body, refType, refId);
            } catch (Exception e) {
                log.error("Lỗi gửi thông báo đến tenant {}: {}", tenantId, e.getMessage());
            }
        });
    }

    

        /**
     * Chức năng: Gửi notification.
     */
public void sendNotification(Long hostId, Long tenantId, String type, String title, String body,
                                 String refType, Long refId) {
        if (tenantId != null) {
            
            sendToTenant(tenantId, type, title, body, refType, refId);
        } else {
            
            sendToAllTenantsByHost(hostId, type, title, body, refType, refId);
        }
    }
}
