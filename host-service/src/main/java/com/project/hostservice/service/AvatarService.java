package com.project.hostservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service xử lý cập nhật avatar người dùng.
 *
 * Quy trình:
 * 1. Validate file (chỉ nhận image/*, tối đa 5 MB)
 * 2. Upload lên Cloudinary folder "smartroom/avatars" với eager transform 400×400
 * 3. Xoá ảnh cũ trên Cloudinary nếu có (tránh tốn storage)
 * 4. Cập nhật users.avatar_url trong database
 * 5. Trả về URL mới cho Flutter lưu vào SharedPreferences
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public String updateAvatar(Long userId, MultipartFile file) {
        // 1. Validate
        validateImageFile(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        // 2. Upload ảnh mới lên Cloudinary
        String newUrl = uploadToCloudinary(file, userId);

        // 3. Xoá ảnh cũ nếu có và là URL Cloudinary
        String oldUrl = user.getAvatarUrl();
        if (oldUrl != null && oldUrl.contains("res.cloudinary.com")) {
            try {
                String publicId = extractPublicId(oldUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Xoá avatar cũ thành công: {}", publicId);
            } catch (Exception e) {
                // Không nên throw — ảnh mới đã upload thành công
                log.warn("Không xoá được avatar cũ {}: {}", oldUrl, e.getMessage());
            }
        }

        // 4. Cập nhật database
        user.setAvatarUrl(newUrl);
        userRepository.save(user);

        return newUrl;
    }

    public void removeAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        String oldUrl = user.getAvatarUrl();
        if (oldUrl != null && oldUrl.contains("res.cloudinary.com")) {
            try {
                cloudinary.uploader().destroy(extractPublicId(oldUrl), ObjectUtils.emptyMap());
            } catch (Exception e) {
                log.warn("Không xoá được avatar {}: {}", oldUrl, e.getMessage());
            }
        }

        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String uploadToCloudinary(MultipartFile file, Long userId) {
        try {
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/avatars",
                            "public_id", "user_" + userId,  // overwrite ảnh cũ cùng userId
                            "overwrite", true,
                            "resource_type", "image",
                            // Tự động crop 400×400, focus vào khuôn mặt
                            "transformation", "c_fill,g_face,h_400,w_400,q_auto,f_auto"
                    )
            );
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Upload avatar thất bại cho userId {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Lỗi upload avatar: " + e.getMessage(), e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Chỉ chấp nhận file ảnh (image/*). Nhận được: " + contentType);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Kích thước file không được vượt quá 5 MB. File hiện tại: "
                            + (file.getSize() / 1024 / 1024) + " MB");
        }
    }

    /**
     * Trích xuất publicId từ Cloudinary URL.
     * Ví dụ: https://res.cloudinary.com/dbj3kf54f/image/upload/v1234/smartroom/avatars/user_5.jpg
     *        → smartroom/avatars/user_5
     */
    private String extractPublicId(String url) {
        // Lấy phần sau "/upload/v{version}/"
        String[] parts = url.split("/upload/");
        if (parts.length < 2) return url;
        String afterUpload = parts[1];
        // Bỏ version nếu có (v1234567/)
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
        }
        // Bỏ đuôi file
        int dotIdx = afterUpload.lastIndexOf('.');
        return dotIdx > 0 ? afterUpload.substring(0, dotIdx) : afterUpload;
    }
}