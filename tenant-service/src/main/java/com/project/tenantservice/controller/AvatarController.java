package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.service.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Cho phép người thuê cập nhật avatar của chính mình.
 * tenant-service không có Cloudinary nên gọi qua host-service upload endpoint,
 * hoặc dùng chung Cloudinary bean nếu cấu hình.
 *
 * Hiện tại: lưu trực tiếp URL Cloudinary vào DB (nếu tenant-service thêm
 * cloudinary dependency) hoặc chỉ nhận URL string từ Flutter sau khi Flutter
 * tự upload lên Cloudinary bằng unsigned preset.
 *
 * Approach đơn giản nhất (không cần Cloudinary ở tenant-service):
 *   Flutter tự upload ảnh lên Cloudinary bằng unsigned upload preset,
 *   lấy URL về, rồi PUT URL đó vào /api/tenant/profile/avatar.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    /**
     * Cập nhật avatar qua URL (sau khi Flutter đã upload lên Cloudinary).
     * Body: { "avatarUrl": "https://res.cloudinary.com/..." }
     */
    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateAvatarUrl(
            @RequestParam Long userId,
            @RequestBody AvatarUrlRequest request) {
        log.info("PUT /api/tenant/avatar - userId: {}", userId);
        String url = avatarService.updateAvatarUrl(userId, request.getAvatarUrl());
        log.info("PUT /api/tenant/avatar - userId: {} cập nhật thành công", userId);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    /**
     * Upload avatar trực tiếp (nếu tenant-service có Cloudinary bean).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/tenant/avatar - userId: {}, size: {} bytes",
                userId, file.getSize());
        String url = avatarService.uploadAvatar(userId, file);
        log.info("POST /api/tenant/avatar - userId: {} upload thành công: {}", userId, url);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeAvatar(@RequestParam Long userId) {
        log.info("DELETE /api/tenant/avatar - userId: {}", userId);
        avatarService.removeAvatar(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ── Inner DTO ─────────────────────────────────────────────
    public static class AvatarUrlRequest {
        private String avatarUrl;
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}