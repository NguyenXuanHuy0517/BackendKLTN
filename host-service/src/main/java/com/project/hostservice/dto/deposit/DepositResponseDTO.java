package com.project.hostservice.dto.deposit;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến deposit để trao đổi giữa các tầng.
 */
@Data
public class DepositResponseDTO {
    private Long depositId;
    private String tenantName;
    private String roomCode;
    private BigDecimal amount;
    private LocalDate expectedCheckIn;
    private String status;
    private String note;
    private LocalDateTime depositDate;
}
