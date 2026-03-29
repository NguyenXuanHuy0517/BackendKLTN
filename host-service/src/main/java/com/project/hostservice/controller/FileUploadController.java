package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller xử lý upload & load ảnh.
 *
 * Tách thành 2 nhóm:
 *  - /api/host/upload/avatar  → ảnh đại diện người dùng (1 file, trả về URL)
 *  - /api/host/upload/room    → ảnh phòng trọ (1 hoặc nhiều file, trả về List<URL>)
 *
 * Ảnh được lưu trên Cloudinary, URL trả về là secure_url từ Cloudinary.
 * Không cần endpoint "load" riêng vì URL Cloudinary có thể truy cập thẳng từ client.
 * Tuy nhiên, endpoint GET tiện dụng để lấy lại URL theo publicId khi cần.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    // ─────────────────────────────────────────────────────────────────────────
    // AVATAR
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Upload ảnh đại diện (avatar).
     *
     * POST /api/host/upload/avatar
     * Content-Type: multipart/form-data
     * Body: file (image/*)
     *
     * Response: URL ảnh trên Cloudinary (String)
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/host/upload/avatar - fileName: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        validateImageFile(file);

        String url = fileUploadService.uploadAvatar(file);

        log.info("POST /api/host/upload/avatar - upload thành công: {}", url);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    /**
     * Xoá ảnh avatar khỏi Cloudinary theo publicId.
     *
     * DELETE /api/host/upload/avatar?publicId=smartroom/avatars/abc123
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(
            @RequestParam String publicId) {

        log.info("DELETE /api/host/upload/avatar - publicId: {}", publicId);
        fileUploadService.deleteFile(publicId);
        log.info("DELETE /api/host/upload/avatar - xoá thành công: {}", publicId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROOM IMAGES
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Upload 1 ảnh phòng trọ.
     *
     * POST /api/host/upload/room
     * Content-Type: multipart/form-data
     * Body: file (image/*)
     *
     * Response: URL ảnh trên Cloudinary (String)
     */
    @PostMapping(value = "/room", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadRoomImage(
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/host/upload/room - fileName: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        validateImageFile(file);

        String url = fileUploadService.uploadRoomImage(file);

        log.info("POST /api/host/upload/room - upload thành công: {}", url);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    /**
     * Upload nhiều ảnh phòng trọ cùng lúc (tối đa 10 ảnh).
     *
     * POST /api/host/upload/room/batch
     * Content-Type: multipart/form-data
     * Body: files (image/*[])
     *
     * Response: List<URL> các ảnh đã upload
     */
    @PostMapping(value = "/room/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> uploadRoomImages(
            @RequestParam("files") List<MultipartFile> files) {

        log.info("POST /api/host/upload/room/batch - {} files", files.size());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("Danh sách file không được rỗng");
        }
        if (files.size() > 10) {
            throw new IllegalArgumentException("Tối đa 10 ảnh mỗi lần upload");
        }

        files.forEach(this::validateImageFile);

        List<String> urls = fileUploadService.uploadRoomImages(files);

        log.info("POST /api/host/upload/room/batch - upload thành công {} ảnh", urls.size());
        return ResponseEntity.ok(ApiResponse.success(urls));
    }

    /**
     * Xoá ảnh phòng khỏi Cloudinary theo publicId.
     *
     * DELETE /api/host/upload/room?publicId=smartroom/rooms/abc123
     */
    @DeleteMapping("/room")
    public ResponseEntity<ApiResponse<Void>> deleteRoomImage(
            @RequestParam String publicId) {

        log.info("DELETE /api/host/upload/room - publicId: {}", publicId);
        fileUploadService.deleteFile(publicId);
        log.info("DELETE /api/host/upload/room - xoá thành công: {}", publicId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────────────────

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Chỉ chấp nhận file ảnh (image/*). File nhận được: " + contentType);
        }
        // Giới hạn 5 MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5 MB");
        }
    }
}