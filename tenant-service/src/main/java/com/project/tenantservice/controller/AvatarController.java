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
 *
 * Flow 1 - Upload file trực tiếp:
 *   Flutter → POST /api/tenant/avatar?userId=X (multipart file)
 *          ← { success: true, data: "https://res.cloudinary.com/..." }
 *
 * Flow 2 - Cập nhật via URL (Flutter tự upload lên Cloudinary):
 *   Flutter → PUT /api/tenant/avatar?userId=X (JSON body với avatarUrl)
 *          ← { success: true, data: "https://res.cloudinary.com/..." }
 *
 * Flutter sau đó lưu URL mới vào SharedPreferences và hiển thị lại avatar.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    /**
     * Upload avatar mới cho user.
     * File được upload lên Cloudinary, URL được lưu vào users.avatar_url.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/tenant/avatar - userId: {}, fileName: {}, size: {} bytes",
                userId, file.getOriginalFilename(), file.getSize());

        String avatarUrl = avatarService.updateAvatar(userId, file);

        log.info("POST /api/tenant/avatar - userId: {} cập nhật avatar thành công: {}", userId, avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

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
     * Xoá avatar — đặt lại về null (dùng ảnh mặc định trên Flutter).
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeAvatar(@RequestParam Long userId) {
        log.info("DELETE /api/tenant/avatar - userId: {}", userId);
        avatarService.removeAvatar(userId);
        log.info("DELETE /api/tenant/avatar - userId: {} xoá avatar thành công", userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ── Inner DTO ─────────────────────────────────────────────
    public static class AvatarUrlRequest {
        private String avatarUrl;
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}