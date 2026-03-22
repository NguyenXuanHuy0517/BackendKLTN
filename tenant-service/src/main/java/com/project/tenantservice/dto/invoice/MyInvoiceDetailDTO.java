package com.project.tenantservice.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MyInvoiceDetailDTO {
    private Long invoiceId;
    private String invoiceCode;
    private String roomCode;
    private int billingMonth;
    private int billingYear;
    private LocalDate dueDate;

    private int elecOld;
    private int elecNew;
    private BigDecimal elecPrice;
    private BigDecimal elecAmount;

    private int waterOld;
    private int waterNew;
    private BigDecimal waterPrice;
    private BigDecimal waterAmount;

    private BigDecimal rentAmount;
    private BigDecimal serviceAmount;
    private List<String> serviceNames;
    private BigDecimal totalAmount;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}