package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.service.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller cho phép host cập nhật avatar của chính mình.
 *
 * Flow:
 *   Flutter → POST /api/host/avatar?userId=X (multipart file)
 *          ← { success: true, data: "https://res.cloudinary.com/..." }
 *
 * Flutter sau đó lưu URL mới vào SharedPreferences và hiển thị lại avatar.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/avatar")
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

        log.info("POST /api/host/avatar - userId: {}, fileName: {}, size: {} bytes",
                userId, file.getOriginalFilename(), file.getSize());

        String avatarUrl = avatarService.updateAvatar(userId, file);

        log.info("POST /api/host/avatar - userId: {} cập nhật avatar thành công: {}", userId, avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

    /**
     * Xoá avatar — đặt lại về null (dùng ảnh mặc định trên Flutter).
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(@RequestParam Long userId) {
        log.info("DELETE /api/host/avatar - userId: {}", userId);
        avatarService.removeAvatar(userId);
        log.info("DELETE /api/host/avatar - userId: {} xoá avatar thành công", userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}