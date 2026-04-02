package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.notification.NotificationRequestDTO;
import com.project.hostservice.dto.notification.NotificationResponseDTO;
import com.project.hostservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ notification và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

        /**
     * Chức năng: Lấy dữ liệu notifications.
     * URL: GET /api/host/notifications
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(@RequestParam Long userId) {
        log.info("GET /api/host/notifications - userId: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUser(userId);
        log.info("GET /api/host/notifications - trả về {} thông báo", notifications.size());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark as read.
     * URL: PATCH /api/host/notifications/{notificationId}/read
     */
@PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        log.info("PATCH /api/host/notifications/{}/read", notificationId);
        notificationService.markAsRead(notificationId);
        log.info("PATCH /api/host/notifications/{}/read - đánh dấu đã đọc thành công", notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ mark all as read.
     * URL: PATCH /api/host/notifications/mark-all-read
     */
@PatchMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam Long userId) {
        log.info("PATCH /api/host/notifications/mark-all-read - userId: {}", userId);
        notificationService.markAllAsRead(userId);
        log.info("PATCH /api/host/notifications/mark-all-read - đánh dấu tất cả đã đọc thành công");
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    

        /**
     * Chức năng: Gửi to tenant.
     * URL: POST /api/host/notifications/send-to-tenant/{tenantId}
     */
@PostMapping("/send-to-tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> sendToTenant(
            @RequestParam Long hostId,
            @PathVariable Long tenantId,
            @Valid @RequestBody NotificationRequestDTO request) {
        log.info("POST /api/host/notifications/send-to-tenant/{} - hostId: {}", tenantId, hostId);

        notificationService.sendToTenant(
                tenantId,
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getRefType(),
                request.getRefId()
        );

        log.info("POST /api/host/notifications/send-to-tenant/{} - gửi thành công", tenantId);
        return ResponseEntity.ok(ApiResponse.success(null, "Gửi thông báo đến tenant thành công"));
    }

    

        /**
     * Chức năng: Gửi to all tenants.
     * URL: POST /api/host/notifications/send-to-all
     */
@PostMapping("/send-to-all")
    public ResponseEntity<ApiResponse<Void>> sendToAllTenants(
            @RequestParam Long hostId,
            @Valid @RequestBody NotificationRequestDTO request) {
        log.info("POST /api/host/notifications/send-to-all - hostId: {}", hostId);

        notificationService.sendToAllTenantsByHost(
                hostId,
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getRefType(),
                request.getRefId()
        );

        log.info("POST /api/host/notifications/send-to-all - gửi thành công");
        return ResponseEntity.ok(ApiResponse.success(null, "Gửi thông báo đến tất cả tenants thành công"));
    }

    

        /**
     * Chức năng: Gửi notification.
     * URL: POST /api/host/notifications/send
     */
@PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendNotification(
            @RequestParam Long hostId,
            @Valid @RequestBody NotificationRequestDTO request) {
        log.info("POST /api/host/notifications/send - hostId: {}, tenantId: {}, type: {}",
                 hostId, request.getTenantId(), request.getType());

        notificationService.sendNotification(
                hostId,
                request.getTenantId(),
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getRefType(),
                request.getRefId()
        );

        String message = request.getTenantId() != null
                ? "Gửi thông báo đến tenant thành công"
                : "Gửi thông báo đến tất cả tenants thành công";

        log.info("POST /api/host/notifications/send - {}", message);
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }
}
