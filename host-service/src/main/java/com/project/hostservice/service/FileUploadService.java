package com.project.hostservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service xử lý upload/delete file lên Cloudinary.
 *
 * Folder structure trên Cloudinary:
 *   smartroom/avatars/  → ảnh đại diện người dùng
 *   smartroom/rooms/    → ảnh phòng trọ
 *   smartroom/documents/→ tài liệu (PDF, Word, ...)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    // ─────────────────────────────────────────────────────────────────────────
    // AVATAR
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Upload ảnh đại diện lên Cloudinary folder "smartroom/avatars".
     * Ảnh được nén về 400×400 px phía server Cloudinary (eager transform).
     *
     * @return secure_url của ảnh đã upload
     */
    public String uploadAvatar(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/avatars",
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            // Tự động crop về 400×400, giữ khuôn mặt ở trung tâm
                            "transformation", "c_fill,g_face,h_400,w_400,q_auto,f_auto"
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("uploadAvatar thành công: {} → {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("uploadAvatar thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload avatar: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROOM IMAGES
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Upload 1 ảnh phòng trọ lên Cloudinary folder "smartroom/rooms".
     * Ảnh được tối ưu chất lượng tự động, giữ nguyên tỷ lệ gốc.
     *
     * @return secure_url của ảnh đã upload
     */
    public String uploadRoomImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/rooms",
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            // Tối ưu chất lượng và format tự động, giới hạn chiều rộng 1280px
                            "transformation", "w_1280,c_limit,q_auto,f_auto"
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("uploadRoomImage thành công: {} → {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("uploadRoomImage thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload ảnh phòng: " + e.getMessage(), e);
        }
    }

    /**
     * Upload nhiều ảnh phòng trọ cùng lúc.
     * Mỗi ảnh được xử lý độc lập; nếu 1 ảnh thất bại sẽ throw exception ngay.
     *
     * @return Danh sách secure_url tương ứng theo thứ tự files đầu vào
     */
    public List<String> uploadRoomImages(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadRoomImage(file));
        }
        return urls;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DOCUMENT (giữ lại để dùng cho các mục đích khác)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Upload tài liệu (PDF, Word, Excel, text) lên folder "smartroom/documents".
     *
     * @return secure_url của tài liệu đã upload
     */
    public String uploadDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }
        if (!isValidDocumentType(file.getContentType())) {
            throw new IllegalArgumentException("Định dạng tài liệu không được hỗ trợ");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/documents",
                            "resource_type", "auto",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("uploadDocument thành công: {} → {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("uploadDocument thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload tài liệu: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Xoá file khỏi Cloudinary theo publicId.
     * publicId là phần path không bao gồm đuôi file,
     * ví dụ: "smartroom/avatars/abc123" hoặc "smartroom/rooms/xyz789"
     */
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("deleteFile thành công: {}", publicId);
        } catch (Exception e) {
            log.error("deleteFile thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi xoá file: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isValidDocumentType(String contentType) {
        if (contentType == null) return false;
        return contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml")
                || contentType.equals("application/vnd.ms-excel")
                || contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml")
                || contentType.equals("text/plain");
    }
}