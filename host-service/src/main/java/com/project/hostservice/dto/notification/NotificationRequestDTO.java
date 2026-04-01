package com.project.hostservice.dto.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String body;

    @NotBlank(message = "Loại thông báo không được để trống")
    private String type;

    private String refType;

    private Long refId;

    // Nếu null: gửi đến tất cả tenants
    // Nếu có giá trị: gửi đến tenant cụ thể
    private Long tenantId;
}
