package com.project.hostservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File phải là hình ảnh (image/*)");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/images",
                            "resource_type", "auto",
                            "use_filename", true
                    )
            );

            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");
            log.info("Upload image thành công: {} -> {}", file.getOriginalFilename(), url);
            return url;
        } catch (Exception e) {
            log.error("Lỗi upload image: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload hình ảnh: " + e.getMessage(), e);
        }
    }

    public String uploadDocument(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }

        String contentType = file.getContentType();
        if (!isValidDocumentType(contentType)) {
            throw new IllegalArgumentException("Định dạng tệp không được hỗ trợ");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/documents",
                            "resource_type", "auto",
                            "use_filename", true
                    )
            );

            String url = (String) uploadResult.get("secure_url");
            log.info("Upload document thành công: {} -> {}", file.getOriginalFilename(), url);
            return url;
        } catch (Exception e) {
            log.error("Lỗi upload document: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload tệp: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Xóa file thành công: {}", publicId);
        } catch (Exception e) {
            log.error("Lỗi xóa file: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi xóa tệp: " + e.getMessage(), e);
        }
    }

    private boolean isValidDocumentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml")
                || contentType.equals("application/vnd.ms-excel")
                || contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml")
                || contentType.equals("text/plain");
    }
}

