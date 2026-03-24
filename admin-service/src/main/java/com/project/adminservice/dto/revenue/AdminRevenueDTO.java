package com.project.adminservice.dto.revenue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRevenueDTO {
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;
    private Map<String, BigDecimal> revenueByPeriod;  // month/quarter/year breakdown
}

