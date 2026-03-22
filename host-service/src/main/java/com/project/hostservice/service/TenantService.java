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

    public List<TenantResponseDTO> getTenantsByHost(Long hostId) {
        return userRepository.findByRole_RoleName("TENANT").stream()
                .map(user -> {
                    TenantResponseDTO dto = tenantMapper.toDTO(user);
                    contractRepository.findByTenant_UserId(user.getUserId()).stream()
                            .filter(c -> c.getStatus().equals("ACTIVE"))
                            .findFirst()
                            .ifPresent(contract -> {
                                dto.setCurrentRoomCode(contract.getRoom().getRoomCode());
                                dto.setContractStatus(contract.getStatus());
                            });
                    return dto;
                })
                .toList();
    }

    public TenantResponseDTO getTenantDetail(Long tenantId) {
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người thuê: " + tenantId));

        TenantResponseDTO dto = tenantMapper.toDTO(user);
        contractRepository.findByTenant_UserId(tenantId).stream()
                .filter(c -> c.getStatus().equals("ACTIVE"))
                .findFirst()
                .ifPresent(contract -> {
                    dto.setCurrentRoomCode(contract.getRoom().getRoomCode());
                    dto.setContractStatus(contract.getStatus());
                });
        return dto;
    }

    public TenantResponseDTO createTenant(TenantCreateDTO request) {
        Role role = roleRepository.findByRoleName("TENANT")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role TENANT"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người thuê: " + tenantId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}