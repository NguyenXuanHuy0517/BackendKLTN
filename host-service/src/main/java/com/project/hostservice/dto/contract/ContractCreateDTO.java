package com.project.hostservice.dto.contract;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractCreateDTO {
    private Long tenantId;
    private Long roomId;
    private Long depositId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPriceOverride;
    private BigDecimal waterPriceOverride;
    private String penaltyTerms;
}