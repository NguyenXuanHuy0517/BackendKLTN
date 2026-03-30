package com.project.tenantservice.service;

import com.project.datalayer.entity.User;
import com.project.datalayer.repository.UserRepository;
import com.project.tenantservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service cập nhật avatar cho tenant.
 *
 * Hỗ trợ 2 phương thức:
 *  1. updateAvatarUrl(userId, url) — Flutter tự upload Cloudinary, truyền URL về
 *  2. uploadAvatar(userId, file)   — tenant-service tự upload (cần Cloudinary bean)
 *
 * Nếu tenant-service không có Cloudinary dependency, phương thức uploadAvatar
 * sẽ ném UnsupportedOperationException và Flutter nên dùng phương thức 1.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final UserRepository userRepository;

    /**
     * Lưu URL avatar do Flutter cung cấp (sau khi Flutter upload lên Cloudinary).
     */
    public String updateAvatarUrl(Long userId, String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            throw new IllegalArgumentException("URL avatar không được rỗng");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    /**
     * Upload file trực tiếp — cần Cloudinary bean.
     * Nếu tenant-service không có Cloudinary, sử dụng updateAvatarUrl thay thế.
     */
    public String uploadAvatar(Long userId, MultipartFile file) {
        // Nếu muốn dùng Cloudinary trong tenant-service:
        // 1. Thêm dependency cloudinary-http44 vào tenant-service/pom.xml
        // 2. Tạo CloudinaryConfig tương tự host-service
        // 3. Inject Cloudinary bean vào đây và upload như AvatarService trong host-service
        //
        // Tạm thời fallback: yêu cầu client dùng PUT /api/tenant/avatar với URL
        throw new UnsupportedOperationException(
                "tenant-service chưa cấu hình Cloudinary. "
                        + "Vui lòng upload ảnh qua /api/host/upload/avatar, "
                        + "lấy URL về rồi gọi PUT /api/tenant/avatar với URL đó.");
    }

    public void removeAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));
        user.setAvatarUrl(null);
        userRepository.save(user);
    }
}