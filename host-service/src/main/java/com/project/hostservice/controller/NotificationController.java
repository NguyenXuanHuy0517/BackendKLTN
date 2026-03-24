package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.notification.NotificationResponseDTO;
import com.project.hostservice.service.NotificationService;
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
}

