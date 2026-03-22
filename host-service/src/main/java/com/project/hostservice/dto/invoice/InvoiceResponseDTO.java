package com.project.hostservice.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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