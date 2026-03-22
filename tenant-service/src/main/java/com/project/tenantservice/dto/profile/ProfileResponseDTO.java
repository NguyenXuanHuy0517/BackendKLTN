package com.project.tenantservice.dto.profile;

import lombok.Data;

@Data
public class ProfileResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String avatarUrl;
    private String role;
}