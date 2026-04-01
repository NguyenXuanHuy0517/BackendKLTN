package com.project.adminservice.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoomAuditDTO {
    private Long roomId;
    private String roomCode;
    private String areaName;
    private String hostName;
    private String status;
    private BigDecimal basePrice;
    private String currentTenantName;  // Current tenant name if room is rented
    private Long daysWithoutInvoice;   // Days since last invoice (for audit)
}

