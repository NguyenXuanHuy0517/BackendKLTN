package com.project.hostservice.service;

import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.RoleRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.tenant.TenantCreateDTO;
import com.project.hostservice.dto.tenant.TenantResponseDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ContractRepository contractRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantMapper tenantMapper;

    /**
     * Lấy danh sách người thuê thuộc về một chủ trọ (hostId).
     *
     * FIX: Trước đây dùng findByRole_RoleName("TENANT") → trả về TẤT CẢ tenant toàn hệ thống,
     *      không phân biệt host nào quản lý.
     *
     *      Nay: Lấy tất cả hợp đồng của hostId → trích xuất tenant duy nhất từ danh sách đó.
     *      Cách này chỉ trả về tenant đã/đang thuê phòng trong khu trọ của chủ trọ này.
     */
    public List<TenantResponseDTO> getTenantsByHost(Long hostId) {
        // Lấy tất cả hợp đồng (mọi trạng thái) của host → trích xuất tenant unique
        return contractRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .map(contract -> contract.getTenant())
                .distinct() // loại trùng nếu 1 tenant có nhiều hợp đồng
                .map(user -> {
                    TenantResponseDTO dto = tenantMapper.toDTO(user);

                    // Tìm hợp đồng ACTIVE hiện tại (nếu có)
                    contractRepository.findByRoom_RoomIdAndStatus(
                                    // Tìm hợp đồng active của tenant này trong khu trọ của host
                                    user.getUserId(), "ACTIVE")
                            .ifPresent(contract -> {
                                dto.setCurrentRoomCode(contract.getRoom().getRoomCode());
                                dto.setContractStatus(contract.getStatus());
                            });

                    // Nếu cách trên không tìm được, tìm theo tenant_id
                    if (dto.getCurrentRoomCode() == null) {
                        contractRepository.findByTenant_UserId(user.getUserId()).stream()
                                .filter(c -> "ACTIVE".equals(c.getStatus()))
                                // Đảm bảo hợp đồng active này thuộc về host đang đăng nhập
                                .filter(c -> c.getRoom().getArea().getHost()
                                        .getUserId().equals(hostId))
                                .findFirst()
                                .ifPresent(c -> {
                                    dto.setCurrentRoomCode(c.getRoom().getRoomCode());
                                    dto.setContractStatus(c.getStatus());
                                });
                    }

                    return dto;
                })
                .toList();
    }

    public TenantResponseDTO getTenantDetail(Long tenantId) {
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người thuê: " + tenantId));

        TenantResponseDTO dto = tenantMapper.toDTO(user);

        contractRepository.findByTenant_UserId(tenantId).stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .findFirst()
                .ifPresent(contract -> {
                    dto.setCurrentRoomCode(contract.getRoom().getRoomCode());
                    dto.setContractStatus(contract.getStatus());
                });

        return dto;
    }

    public TenantResponseDTO createTenant(TenantCreateDTO request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }

        Role role = roleRepository.findByRoleName("TENANT")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role TENANT"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);
        return tenantMapper.toDTO(user);
    }

    public void toggleActive(Long tenantId) {
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người thuê: " + tenantId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}