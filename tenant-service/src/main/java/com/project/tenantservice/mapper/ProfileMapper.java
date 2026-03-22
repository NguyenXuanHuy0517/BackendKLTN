package com.project.tenantservice.mapper;

import com.project.datalayer.entity.User;
import com.project.tenantservice.dto.profile.ProfileResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponseDTO toDTO(User user) {
        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIdCardNumber(user.getIdCardNumber());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole().getRoleName());
        return dto;
    }
}