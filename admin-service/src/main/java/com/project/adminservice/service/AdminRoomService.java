package com.project.adminservice.service;

import com.project.adminservice.dto.room.AdminRoomResponseDTO;
import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.Room;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomService {

    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;

    public List<AdminRoomResponseDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<AdminRoomResponseDTO> getRoomsMissingInvoices() {
        // This is a stub - implement based on your requirements
        return roomRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    private AdminRoomResponseDTO mapToDTO(Room room) {
        AdminRoomResponseDTO dto = new AdminRoomResponseDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomCode(room.getRoomCode());
        dto.setAreaName(room.getArea().getAreaName());
        dto.setHostName(room.getArea().getHost().getFullName());
        dto.setStatus(room.getStatus());
        dto.setBasePrice(room.getBasePrice());
        
        // Get current tenant if room is rented
        contractRepository.findByRoom_RoomIdAndStatus(room.getRoomId(), "ACTIVE")
                .ifPresent(contract -> dto.setCurrentTenantName(contract.getTenant().getFullName()));
        
        return dto;
    }
}

