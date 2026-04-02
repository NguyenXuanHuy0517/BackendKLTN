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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

class TenantDashboardServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractServiceRepository contractServiceRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private TenantDashboardService tenantDashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSummaryAggregatesContractAndCounters() {
        Long userId = 7L;
        Contract contract = new Contract();
        contract.setContractId(11L);

        MyContractDTO currentContract = new MyContractDTO();
        currentContract.setContractId(11L);
        currentContract.setContractCode("HD-11");

        when(contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE"))
                .thenReturn(Optional.of(contract));
        when(contractServiceRepository.findByContract_ContractId(11L)).thenReturn(List.of());
        when(contractMapper.toDTO(contract, List.of())).thenReturn(currentContract);
        when(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "UNPAID")).thenReturn(3L);
        when(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "OVERDUE")).thenReturn(1L);
        when(issueRepository.countByTenant_UserIdAndStatusIn(userId, List.of("OPEN", "PROCESSING"))).thenReturn(2L);
        when(notificationRepository.countByUser_UserIdAndIsReadFalse(userId)).thenReturn(4L);

        TenantDashboardSummaryDTO summary = tenantDashboardService.getSummary(userId);

        assertSame(currentContract, summary.getCurrentContract());
        assertEquals(3L, summary.getUnpaidCount());
        assertEquals(1L, summary.getOverdueCount());
        assertEquals(2L, summary.getOpenIssueCount());
        assertEquals(4L, summary.getUnreadCount());
    }

    @Test
    void getSummaryHandlesMissingActiveContract() {
        Long userId = 9L;
        when(contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE"))
                .thenReturn(Optional.empty());
        when(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "UNPAID")).thenReturn(0L);
        when(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "OVERDUE")).thenReturn(0L);
        when(issueRepository.countByTenant_UserIdAndStatusIn(userId, List.of("OPEN", "PROCESSING"))).thenReturn(0L);
        when(notificationRepository.countByUser_UserIdAndIsReadFalse(userId)).thenReturn(0L);

        TenantDashboardSummaryDTO summary = tenantDashboardService.getSummary(userId);

        assertNull(summary.getCurrentContract());
        assertEquals(0L, summary.getUnpaidCount());
        assertEquals(0L, summary.getOverdueCount());
        assertEquals(0L, summary.getOpenIssueCount());
        assertEquals(0L, summary.getUnreadCount());
    }
}
