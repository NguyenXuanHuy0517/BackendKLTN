package com.project.tenantservice.dto.contract;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class MyContractDTO {
    private Long contractId;
    private String contractCode;
    private String roomCode;
    private String areaName;
    private String areaAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private String status;
    private List<String> serviceNames;
    private long daysUntilExpiry;
}