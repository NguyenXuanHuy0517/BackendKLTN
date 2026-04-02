package com.project.tenantservice.service;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.InvoiceRepository;
import com.project.datalayer.repository.IssueRepository;
import com.project.datalayer.repository.NotificationRepository;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.dto.dashboard.TenantDashboardSummaryDTO;
import com.project.tenantservice.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantDashboardService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceRepository invoiceRepository;
    private final IssueRepository issueRepository;
    private final NotificationRepository notificationRepository;
    private final ContractMapper contractMapper;

    public TenantDashboardSummaryDTO getSummary(Long userId) {
        TenantDashboardSummaryDTO summary = new TenantDashboardSummaryDTO();
        contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE")
                .ifPresent(contract -> summary.setCurrentContract(toContractDto(contract)));
        summary.setUnpaidCount(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "UNPAID"));
        summary.setOverdueCount(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "OVERDUE"));
        summary.setOpenIssueCount(issueRepository.countByTenant_UserIdAndStatusIn(userId, List.of("OPEN", "PROCESSING")));
        summary.setUnreadCount(notificationRepository.countByUser_UserIdAndIsReadFalse(userId));
        return summary;
    }

    private MyContractDTO toContractDto(Contract contract) {
        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contract.getContractId())
        );
    }
}
