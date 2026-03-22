package com.project.hostservice.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReportDTO {
    private BigDecimal totalRevenue;
    private BigDecimal previousRevenue;
    private int totalRooms;
    private int rentedRooms;
    private int availableRooms;
    private int maintenanceRooms;
    private double occupancyRate;
    private int overdueCount;
    private int openIssueCount;
    private List<String> topServices;
}