package com.project.adminservice.dto.dashboard;

import com.project.adminservice.dto.common.AdminAlertDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    private List<AdminAlertDTO> alerts;  // NEW - Quick alerts for admin
}

