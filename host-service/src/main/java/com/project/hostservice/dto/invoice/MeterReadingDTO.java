package com.project.hostservice.dto.invoice;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến meter reading để trao đổi giữa các tầng.
 */
@Data
public class MeterReadingDTO {
    private int elecOld;
    private int elecNew;
    private int waterOld;
    private int waterNew;
}
