package com.project.hostservice.service;

import com.project.datalayer.repository.*;
import com.project.hostservice.dto.report.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;
    private final IssueRepository issueRepository;
    private final ContractServiceRepository contractServiceRepository;

    public ReportDTO getDashboard(Long hostId) {
        int totalRooms = roomRepository.findByArea_Host_UserId(hostId).size();
        int rentedRooms = roomRepository.findByArea_Host_UserId(hostId).stream()
                .filter(r -> r.getStatus().equals("RENTED")).toList().size();
        int availableRooms = roomRepository.findByArea_Host_UserId(hostId).stream()
                .filter(r -> r.getStatus().equals("AVAILABLE")).toList().size();
        int maintenanceRooms = roomRepository.findByArea_Host_UserId(hostId).stream()
                .filter(r -> r.getStatus().equals("MAINTENANCE")).toList().size();

        LocalDate now = LocalDate.now();
        BigDecimal totalRevenue = invoiceRepository.findByContract_Room_Area_Host_UserId(hostId).stream()
                .filter(i -> i.getStatus().equals("PAID")
                        && i.getBillingMonth() == now.getMonthValue()
                        && i.getBillingYear() == now.getYear())
                .map(i -> i.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousRevenue = invoiceRepository.findByContract_Room_Area_Host_UserId(hostId).stream()
                .filter(i -> i.getStatus().equals("PAID")
                        && i.getBillingMonth() == now.minusMonths(1).getMonthValue()
                        && i.getBillingYear() == now.minusMonths(1).getYear())
                .map(i -> i.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int overdueCount = (int) invoiceRepository.findByContract_Room_Area_Host_UserId(hostId).stream()
                .filter(i -> i.getStatus().equals("OVERDUE")).count();

        int openIssueCount = (int) issueRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .filter(i -> i.getStatus().equals("OPEN") || i.getStatus().equals("PROCESSING")).count();

        ReportDTO dto = new ReportDTO();
        dto.setTotalRevenue(totalRevenue);
        dto.setPreviousRevenue(previousRevenue);
        dto.setTotalRooms(totalRooms);
        dto.setRentedRooms(rentedRooms);
        dto.setAvailableRooms(availableRooms);
        dto.setMaintenanceRooms(maintenanceRooms);
        dto.setOccupancyRate(totalRooms > 0 ? (double) rentedRooms / totalRooms * 100 : 0);
        dto.setOverdueCount(overdueCount);
        dto.setOpenIssueCount(openIssueCount);
        dto.setTopServices(List.of());
        return dto;
    }
}