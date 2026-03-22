package com.project.hostservice.dto.tenant;

import lombok.Data;

@Data
public class TenantResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private boolean active;
    private String currentRoomCode;
    private String contractStatus;
}