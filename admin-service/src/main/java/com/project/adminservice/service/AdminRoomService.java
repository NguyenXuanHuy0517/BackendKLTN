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

/**
 * Vai trò: Service xử lý nghiệp vụ của module admin-service.
 * Chức năng: Chứa logic xử lý liên quan đến admin room.
 */
@Service
@RequiredArgsConstructor
public class AdminRoomService {

    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

        /**
     * Chức năng: Lấy dữ liệu all rooms.
     */
public List<AdminRoomAuditDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToAuditDTO)
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu rooms missing invoices.
     */
public List<AdminRoomAuditDTO> getRoomsMissingInvoices() {
        
        YearMonth currentMonth = YearMonth.now();

        
        return contractRepository.findByStatus("ACTIVE").stream()
                .filter(contract -> !invoiceRepository.existsByContract_ContractIdAndBillingMonthAndBillingYear(
                        contract.getContractId(), currentMonth.getMonthValue(), currentMonth.getYear()))
                .map(Contract::getRoom)
                .distinct()
                .map(room -> mapToAuditDTOWithoutInvoice(room, currentMonth.getMonthValue(), currentMonth.getYear()))
                .toList();
    }

        /**
     * Chức năng: Ánh xạ to audit dto.
     */
private AdminRoomAuditDTO mapToAuditDTO(Room room) {
        AdminRoomAuditDTO dto = new AdminRoomAuditDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomCode(room.getRoomCode());
        dto.setAreaName(room.getArea().getAreaName());
        dto.setHostName(room.getArea().getHost().getFullName());
        dto.setStatus(room.getStatus());
        dto.setBasePrice(room.getBasePrice());
        
        
        contractRepository.findByRoom_RoomIdAndStatus(room.getRoomId(), "ACTIVE")
                .ifPresent(contract -> dto.setCurrentTenantName(contract.getTenant().getFullName()));
        
        dto.setDaysWithoutInvoice(null);  

        return dto;
    }

        /**
     * Chức năng: Ánh xạ to audit dto without invoice.
     */
private AdminRoomAuditDTO mapToAuditDTOWithoutInvoice(Room room, int month, int year) {
        AdminRoomAuditDTO dto = mapToAuditDTO(room);

        
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        long daysWithoutInvoice = java.time.temporal.ChronoUnit.DAYS.between(firstOfMonth, LocalDate.now());
        dto.setDaysWithoutInvoice(daysWithoutInvoice);

        return dto;
    }
}
