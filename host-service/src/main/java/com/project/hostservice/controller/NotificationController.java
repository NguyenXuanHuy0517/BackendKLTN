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

@Slf4j
@RestController
@RequestMapping("/api/host/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(@RequestParam Long userId) {
        log.info("GET /api/host/notifications - userId: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUser(userId);
        log.info("GET /api/host/notifications - trả về {} thông báo", notifications.size());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        log.info("PATCH /api/host/notifications/{}/read", notificationId);
        notificationService.markAsRead(notificationId);
        log.info("PATCH /api/host/notifications/{}/read - đánh dấu đã đọc thành công", notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam Long userId) {
        log.info("PATCH /api/host/notifications/mark-all-read - userId: {}", userId);
        notificationService.markAllAsRead(userId);
        log.info("PATCH /api/host/notifications/mark-all-read - đánh dấu tất cả đã đọc thành công");
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Gửi thông báo đến một tenant cụ thể
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
     * Gửi thông báo đến tất cả tenants của một host
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
     * Gửi thông báo đến tenant cụ thể hoặc tất cả tenants (nếu tenantId null)
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

