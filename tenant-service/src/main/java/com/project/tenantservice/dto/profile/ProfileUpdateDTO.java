package com.project.tenantservice.dto.profile;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String avatarUrl;
    private String fcmToken;
    private String fullName;
    private String phoneNumber;
}