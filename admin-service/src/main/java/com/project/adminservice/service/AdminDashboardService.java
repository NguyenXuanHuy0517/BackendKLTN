package com.project.adminservice.service;

import com.project.adminservice.dto.common.AdminAlertDTO;
import com.project.adminservice.dto.dashboard.AdminDashboardDTO;
import com.project.datalayer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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
        dashboard.setTotalRooms(roomRepository.countTotalRooms());
        dashboard.setTotalContracts(contractRepository.count());
        
        // Calculate occupancy rate
        Long rentedRooms = roomRepository.countRentedRooms();
        Long totalRooms = dashboard.getTotalRooms();
        long occupancyRate = (totalRooms > 0) ? (rentedRooms * 100) / totalRooms : 0;
        dashboard.setOccupancyRate(occupancyRate);
        
        // Calculate total revenue from paid invoices
        BigDecimal totalRevenue = invoiceRepository.sumRevenueAllTime();
        dashboard.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Calculate this month revenue
        YearMonth now = YearMonth.now();
        BigDecimal thisMonthRevenue = invoiceRepository.sumRevenueByPeriod(now.getMonthValue(), now.getYear());
        dashboard.setThisMonthRevenue(thisMonthRevenue != null ? thisMonthRevenue : BigDecimal.ZERO);

        // Count overdue invoices
        Long overdueInvoices = invoiceRepository.countOverdueInvoices();
        dashboard.setOverdueInvoices(overdueInvoices != null ? overdueInvoices : 0L);

        // Count active contracts
        Long activeContracts = contractRepository.countActiveContracts();
        dashboard.setActiveContracts(activeContracts != null ? activeContracts : 0L);

        // Build alerts
        List<AdminAlertDTO> alerts = buildAlerts(overdueInvoices, activeContracts);
        dashboard.setAlerts(alerts);

        return dashboard;
    }

    private List<AdminAlertDTO> buildAlerts(Long overdueInvoices, Long activeContracts) {
        List<AdminAlertDTO> alerts = new ArrayList<>();

        // Alert: Overdue invoices
        if (overdueInvoices != null && overdueInvoices > 0) {
            AdminAlertDTO alert = new AdminAlertDTO(
                    "OVERDUE_INVOICE",
                    "Có " + overdueInvoices + " hóa đơn quá hạn cần xử lý",
                    Math.toIntExact(overdueInvoices),
                    "ERROR"
            );
            alerts.add(alert);
        }

        // Alert: Rooms without invoice for current month
        YearMonth now = YearMonth.now();
        long roomsWithoutInvoice = countRoomsWithoutInvoiceGlobally(now.getMonthValue(), now.getYear());
        if (roomsWithoutInvoice > 0) {
            AdminAlertDTO alert = new AdminAlertDTO(
                    "MISSING_INVOICE",
                    "Có " + roomsWithoutInvoice + " phòng chưa có hóa đơn kỳ này",
                    Math.toIntExact(roomsWithoutInvoice),
                    "WARNING"
            );
            alerts.add(alert);
        }

        // Alert: Blocked hosts (optional)
        long blockedHosts = userRepository.findByRole_RoleNameAndIsActiveFalse("HOST").size();
        if (blockedHosts > 0) {
            AdminAlertDTO alert = new AdminAlertDTO(
                    "HOST_BLOCKED",
                    "Có " + blockedHosts + " host đang bị khóa",
                    Math.toIntExact(blockedHosts),
                    "INFO"
            );
            alerts.add(alert);
        }

        return alerts;
    }

    private long countRoomsWithoutInvoiceGlobally(int month, int year) {
        // Count all rooms with active contracts that don't have invoice for the month
        return contractRepository.findByStatus("ACTIVE").stream()
                .filter(contract -> !invoiceRepository.existsByContract_ContractIdAndBillingMonthAndBillingYear(
                        contract.getContractId(), month, year))
                .map(contract -> contract.getRoom().getRoomId())
                .distinct()
                .count();
    }
}

