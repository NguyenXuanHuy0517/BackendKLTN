package com.project.hostservice.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến invoice để trao đổi giữa các tầng.
 */
@Data
public class InvoiceResponseDTO {
    private Long invoiceId;
    private String invoiceCode;
    private String tenantName;
    private String roomCode;
    private int billingMonth;
    private int billingYear;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
