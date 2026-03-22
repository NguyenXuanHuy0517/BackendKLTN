package com.project.tenantservice.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MyInvoiceDTO {
    private Long invoiceId;
    private String invoiceCode;
    private int billingMonth;
    private int billingYear;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}