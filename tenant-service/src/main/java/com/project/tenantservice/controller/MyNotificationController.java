package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.notification.NotificationDTO;
import com.project.tenantservice.service.MyNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ my notification và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/notifications")
@RequiredArgsConstructor
public class MyNotificationController {

    private final MyNotificationService notificationService;

        /**
     * Chức năng: Lấy dữ liệu notifications.
     * URL: GET /api/tenant/notifications
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/notifications - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getMyNotifications(userId)));
    }

        /**
     * Chức năng: Lấy dữ liệu unread count.
     * URL: GET /api/tenant/notifications/unread-count
     */
@GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @RequestParam Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.countUnread(userId)));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark as read.
     * URL: PATCH /api/tenant/notifications/{notificationId}/read
     */
@PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        log.info("PATCH /api/tenant/notifications/{}/read - userId: {}",
                notificationId, userId);
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark all as read.
     * URL: PATCH /api/tenant/notifications/read-all
     */
@PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestParam Long userId) {
        log.info("PATCH /api/tenant/notifications/read-all - userId: {}",
                userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
