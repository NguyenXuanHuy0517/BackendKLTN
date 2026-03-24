package com.project.adminservice.service;

import com.project.adminservice.dto.dashboard.AdminDashboardDTO;
import com.project.datalayer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final MotelAreaRepository areaRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    public AdminDashboardDTO getDashboard() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        
        // Count totals
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalHosts((long) userRepository.findByRole_RoleName("HOST").size());
        dashboard.setTotalTenants((long) userRepository.findByRole_RoleName("TENANT").size());
        dashboard.setTotalRooms(roomRepository.count());
        dashboard.setTotalContracts(contractRepository.count());
        
        // Calculate occupancy rate
        Long rentedRooms = roomRepository.findAll().stream()
                .filter(r -> "RENTED".equals(r.getStatus()))
                .count();
        long occupancyRate = (rentedRooms * 100) / Math.max(dashboard.getTotalRooms(), 1);
        dashboard.setOccupancyRate(occupancyRate);
        
        // Calculate revenue (stub - implement based on your Invoice entity)
        dashboard.setTotalRevenue(BigDecimal.ZERO);
        dashboard.setThisMonthRevenue(BigDecimal.ZERO);
        
        // Count overdue invoices (stub)
        dashboard.setOverdueInvoices(0L);
        
        // Count active contracts
        Long activeContracts = contractRepository.findAll().stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .count();
        dashboard.setActiveContracts(activeContracts);
        
        return dashboard;
    }
}

