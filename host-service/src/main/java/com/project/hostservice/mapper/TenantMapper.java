package com.project.hostservice.mapper;

import com.project.datalayer.entity.User;
import com.project.hostservice.dto.tenant.TenantResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantResponseDTO toDTO(User user) {
        TenantResponseDTO dto = new TenantResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIdCardNumber(user.getIdCardNumber());
        dto.setActive(user.isActive());
        return dto;
    }
}