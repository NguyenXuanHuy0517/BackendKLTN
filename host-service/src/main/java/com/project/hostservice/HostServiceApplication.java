package com.project.hostservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Vai trò: Điểm khởi động của module host-service.
 * Chức năng: Khởi tạo và đăng ký các thành phần của ứng dụng host-service.
 */
@SpringBootApplication(scanBasePackages = {
        "com.project.hostservice",
        "com.project.datalayer"
})
@EnableJpaRepositories(basePackages = "com.project.datalayer.repository")
@EnableScheduling
public class HostServiceApplication {
        /**
     * Chức năng: Thực hiện nghiệp vụ main.
     */
public static void main(String[] args) {
        SpringApplication.run(HostServiceApplication.class, args);
    }
}
