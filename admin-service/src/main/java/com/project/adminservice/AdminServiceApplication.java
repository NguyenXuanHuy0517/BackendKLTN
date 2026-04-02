package com.project.adminservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Vai trò: Điểm khởi động của module admin-service.
 * Chức năng: Khởi tạo và đăng ký các thành phần của ứng dụng admin-service.
 */
@SpringBootApplication(scanBasePackages = {"com.project.adminservice", "com.project.datalayer"})
@EnableJpaRepositories(basePackages = {"com.project.datalayer.repository"})
public class AdminServiceApplication {

        /**
     * Chức năng: Thực hiện nghiệp vụ main.
     */
public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

}
