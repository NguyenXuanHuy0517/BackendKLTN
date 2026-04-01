package com.project.adminservice.service;

import com.project.adminservice.dto.room.AdminRoomAuditDTO;
import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.Room;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.InvoiceRepository;
import com.project.datalayer.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomService {

    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    public List<AdminRoomAuditDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToAuditDTO)
                .toList();
    }

    public List<AdminRoomAuditDTO> getRoomsMissingInvoices() {
        // Get current month
        YearMonth currentMonth = YearMonth.now();

        // Find all rooms with active contracts that don't have invoice for current month
        return contractRepository.findByStatus("ACTIVE").stream()
                .filter(contract -> !invoiceRepository.existsByContract_ContractIdAndBillingMonthAndBillingYear(
                        contract.getContractId(), currentMonth.getMonthValue(), currentMonth.getYear()))
                .map(Contract::getRoom)
                .distinct()
                .map(room -> mapToAuditDTOWithoutInvoice(room, currentMonth.getMonthValue(), currentMonth.getYear()))
                .toList();
    }

    private AdminRoomAuditDTO mapToAuditDTO(Room room) {
        AdminRoomAuditDTO dto = new AdminRoomAuditDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomCode(room.getRoomCode());
        dto.setAreaName(room.getArea().getAreaName());
        dto.setHostName(room.getArea().getHost().getFullName());
        dto.setStatus(room.getStatus());
        dto.setBasePrice(room.getBasePrice());
        
        // Get current tenant if room is rented
        contractRepository.findByRoom_RoomIdAndStatus(room.getRoomId(), "ACTIVE")
                .ifPresent(contract -> dto.setCurrentTenantName(contract.getTenant().getFullName()));
        
        dto.setDaysWithoutInvoice(null);  // Not applicable for general list

        return dto;
    }

    private AdminRoomAuditDTO mapToAuditDTOWithoutInvoice(Room room, int month, int year) {
        AdminRoomAuditDTO dto = mapToAuditDTO(room);

        // Calculate days since the 1st of the current month
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        long daysWithoutInvoice = java.time.temporal.ChronoUnit.DAYS.between(firstOfMonth, LocalDate.now());
        dto.setDaysWithoutInvoice(daysWithoutInvoice);

        return dto;
    }
}

