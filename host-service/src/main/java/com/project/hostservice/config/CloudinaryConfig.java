package com.project.hostservice.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Vai trò: File cấu hình của module host-service.
 * Chức năng: Khai báo bean và thiết lập liên quan đến cloudinary.
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

        /**
     * Chức năng: Thực hiện nghiệp vụ cloudinary.
     */
@Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(String.format(
                "cloudinary://%s:%s@%s",
                apiKey,
                apiSecret,
                cloudName
        ));
    }
}
