package com.project.hostservice.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDetailDTO {
    private Long invoiceId;
    private String invoiceCode;
    private String tenantName;
    private String roomCode;
    private int billingMonth;
    private int billingYear;
    private BigDecimal rentAmount;
    private int elecOld;
    private int elecNew;
    private BigDecimal elecPrice;
    private BigDecimal elecAmount;
    private int waterOld;
    private int waterNew;
    private BigDecimal waterPrice;
    private BigDecimal waterAmount;
    private BigDecimal serviceAmount;
    private List<String> serviceNames;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}