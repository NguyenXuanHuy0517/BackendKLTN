package com.project.adminservice.service;

import com.project.adminservice.dto.host.AdminHostResponseDTO;
import com.project.adminservice.exception.ResourceNotFoundException;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.MotelAreaRepository;
import com.project.datalayer.repository.RoomRepository;
import com.project.datalayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminHostService {

    private final UserRepository userRepository;
    private final MotelAreaRepository areaRepository;
    private final RoomRepository roomRepository;

    public List<AdminHostResponseDTO> getAllHosts() {
        return userRepository.findByRole_RoleName("HOST").stream()
                .map(this::mapToDTO)
                .toList();
    }

    public AdminHostResponseDTO getHostDetail(Long hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy host: " + hostId));
        return mapToDTO(host);
    }

    public void toggleActive(Long hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy host: " + hostId));
        host.setActive(!host.isActive());
        userRepository.save(host);
    }

    private AdminHostResponseDTO mapToDTO(User host) {
        AdminHostResponseDTO dto = new AdminHostResponseDTO();
        dto.setUserId(host.getUserId());
        dto.setFullName(host.getFullName());
        dto.setEmail(host.getEmail());
        dto.setPhoneNumber(host.getPhoneNumber());
        dto.setAvatarUrl(host.getAvatarUrl());
        dto.setActive(host.isActive());
        
        // Count areas and rooms
        Long totalAreas = (long) areaRepository.findByHost_UserId(host.getUserId()).size();
        dto.setTotalAreas(totalAreas);
        
        Long totalRooms = (long) roomRepository.findByArea_Host_UserId(host.getUserId()).size();
        dto.setTotalRooms(totalRooms);
        
        return dto;
    }
}

