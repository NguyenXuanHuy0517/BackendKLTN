package com.project.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Vai trò: Điểm khởi động của module Backend.
 * Chức năng: Khởi tạo và đăng ký các thành phần của ứng dụng Backend.
 */
@SpringBootApplication(scanBasePackages = {
    "com.project.backend",
    "com.project.authservice",
    "com.project.hostservice", 
    "com.project.tenantservice",
    "com.project.adminservice",
    "com.project.datalayer"
})
public class BackendApplication {

        /**
     * Chức năng: Thực hiện nghiệp vụ main.
     */
public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
