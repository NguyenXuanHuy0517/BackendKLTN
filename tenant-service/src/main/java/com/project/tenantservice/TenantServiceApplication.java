package com.project.tenantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Vai trò: Điểm khởi động của module tenant-service.
 * Chức năng: Khởi tạo và đăng ký các thành phần của ứng dụng tenant-service.
 */
@SpringBootApplication(scanBasePackages = {
        "com.project.tenantservice",
        "com.project.datalayer"
})
@EnableJpaRepositories(basePackages = "com.project.datalayer.repository")
public class TenantServiceApplication {
        /**
     * Chức năng: Thực hiện nghiệp vụ main.
     */
public static void main(String[] args) {
        SpringApplication.run(TenantServiceApplication.class, args);
    }
}
