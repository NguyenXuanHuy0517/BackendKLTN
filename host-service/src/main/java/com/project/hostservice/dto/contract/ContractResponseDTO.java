package com.project.hostservice.dto.contract;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ContractResponseDTO {
    private Long contractId;
    private String contractCode;
    private String tenantName;
    private String roomCode;
    private String areaName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPriceOverride;
    private BigDecimal waterPriceOverride;
    private String status;
    private List<String> serviceNames;
}