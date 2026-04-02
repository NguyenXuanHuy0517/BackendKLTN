package com.project.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Vai trò: Điểm khởi động của module auth-service.
 * Chức năng: Khởi tạo và đăng ký các thành phần của ứng dụng auth-service.
 */
@SpringBootApplication(scanBasePackages = {
        "com.project.authservice",
        "com.project.datalayer"
})
@EnableJpaRepositories(basePackages = "com.project.datalayer.repository")
@PropertySource("classpath:application.properties")  
public class AuthServiceApplication {
        /**
     * Chức năng: Thực hiện nghiệp vụ main.
     */
public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
