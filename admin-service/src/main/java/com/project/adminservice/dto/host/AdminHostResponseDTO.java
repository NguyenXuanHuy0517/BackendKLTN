package com.project.adminservice.dto.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHostResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private boolean isActive;
    private Long totalAreas;
    private Long totalRooms;
}

