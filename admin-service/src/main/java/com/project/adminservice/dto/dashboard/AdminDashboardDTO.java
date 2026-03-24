package com.project.adminservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private Long totalUsers;
    private Long totalHosts;
    private Long totalTenants;
    private Long totalRooms;
    private Long totalContracts;
    private Long occupancyRate;  // Percentage
    private BigDecimal totalRevenue;
    private BigDecimal thisMonthRevenue;
    private Long overdueInvoices;
    private Long activeContracts;
}

