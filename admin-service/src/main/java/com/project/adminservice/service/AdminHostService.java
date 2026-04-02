package com.project.adminservice.service;

import com.project.adminservice.dto.host.AdminHostDetailDTO;
import com.project.adminservice.dto.host.AdminHostResponseDTO;
import com.project.adminservice.dto.host.AdminHostStatusUpdateRequest;
import com.project.adminservice.exception.ResourceNotFoundException;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module admin-service.
 * Chức năng: Chứa logic xử lý liên quan đến admin host.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminHostService {

    private final UserRepository userRepository;
    private final MotelAreaRepository areaRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

        /**
     * Chức năng: Lấy dữ liệu all hosts.
     */
public List<AdminHostResponseDTO> getAllHosts() {
        return userRepository.findByRole_RoleName("HOST").stream()
                .map(this::mapToListDTO)
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu host detail.
     */
public AdminHostDetailDTO getHostDetail(Long hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy host: " + hostId));

        if (!"HOST".equals(host.getRole().getRoleName())) {
            throw new IllegalArgumentException("User này không phải host");
        }

        return mapToDetailDTO(host);
    }

        /**
     * Chức năng: Cập nhật host status.
     */
@Transactional
    public void updateHostStatus(Long hostId, AdminHostStatusUpdateRequest request) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy host: " + hostId));

        if (!"HOST".equals(host.getRole().getRoleName())) {
            throw new IllegalArgumentException("User này không phải host");
        }

        
        if ("ADMIN".equals(host.getRole().getRoleName())) {
            throw new IllegalArgumentException("Không thể khóa tài khoản ADMIN");
        }

        
        if (host.isActive() == request.isActive()) {
            log.warn("Host {} đã ở trạng thái target {}", hostId, request.isActive());
            return;
        }

        host.setActive(request.isActive());
        userRepository.save(host);

        log.info("Host {} status updated to {} by admin. Reason: {}. Note: {}",
                hostId, request.isActive(), request.getReason(), request.getNote());
    }

        /**
     * Chức năng: Ánh xạ to list dto.
     */
private AdminHostResponseDTO mapToListDTO(User host) {
        AdminHostResponseDTO dto = new AdminHostResponseDTO();
        dto.setUserId(host.getUserId());
        dto.setFullName(host.getFullName());
        dto.setEmail(host.getEmail());
        dto.setPhoneNumber(host.getPhoneNumber());
        dto.setAvatarUrl(host.getAvatarUrl());
        dto.setActive(host.isActive());
        
        Long totalAreas = (long) areaRepository.findByHost_UserId(host.getUserId()).size();
        dto.setTotalAreas(totalAreas);

        Long totalRooms = (long) roomRepository.findByArea_Host_UserId(host.getUserId()).size();
        dto.setTotalRooms(totalRooms);

        return dto;
    }

        /**
     * Chức năng: Ánh xạ to detail dto.
     */
private AdminHostDetailDTO mapToDetailDTO(User host) {
        AdminHostDetailDTO dto = new AdminHostDetailDTO();
        dto.setUserId(host.getUserId());
        dto.setFullName(host.getFullName());
        dto.setEmail(host.getEmail());
        dto.setPhoneNumber(host.getPhoneNumber());
        dto.setAvatarUrl(host.getAvatarUrl());
        dto.setActive(host.isActive());
        dto.setCreatedAt(host.getCreatedAt());

        
        Long totalAreas = (long) areaRepository.findByHost_UserId(host.getUserId()).size();
        dto.setTotalAreas(totalAreas);
        
        
        Long totalRooms = (long) roomRepository.findByArea_Host_UserId(host.getUserId()).size();
        dto.setTotalRooms(totalRooms);
        
        
        Long activeContracts = contractRepository.countByRoom_Area_Host_UserIdAndStatus(host.getUserId(), "ACTIVE");
        dto.setActiveContracts(activeContracts);

        
        Long overdueInvoices = invoiceRepository.countOverdueByHostId(host.getUserId());
        dto.setOverdueInvoices(overdueInvoices);

        
        YearMonth currentMonth = YearMonth.now();
        Long roomsWithoutInvoice = roomRepository.countRoomsWithoutInvoiceByHostId(
                host.getUserId(),
                currentMonth.getMonthValue(),
                currentMonth.getYear()
        );
        dto.setRoomsWithoutInvoice(roomsWithoutInvoice);

        dto.setLatestStatusReason(null);  

        return dto;
    }
}
