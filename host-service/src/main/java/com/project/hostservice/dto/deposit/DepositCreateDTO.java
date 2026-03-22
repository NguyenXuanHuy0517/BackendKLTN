package com.project.hostservice.dto.deposit;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DepositCreateDTO {
    private Long tenantId;
    private Long roomId;
    private BigDecimal amount;
    private LocalDate expectedCheckIn;
    private String note;
}