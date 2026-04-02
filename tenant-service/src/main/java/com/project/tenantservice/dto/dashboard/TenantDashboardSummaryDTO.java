package com.project.tenantservice.dto.dashboard;

import com.project.tenantservice.dto.contract.MyContractDTO;
import lombok.Data;

@Data
public class TenantDashboardSummaryDTO {
    private MyContractDTO currentContract;
    private long unpaidCount;
    private long overdueCount;
    private long openIssueCount;
    private long unreadCount;
}
