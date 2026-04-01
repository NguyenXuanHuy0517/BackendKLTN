package com.project.adminservice.dto.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHostDetailDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private boolean isActive;
    private Long totalAreas;
    private Long totalRooms;
    private Long activeContracts;
    private Long overdueInvoices;
    private Long roomsWithoutInvoice;
    private String latestStatusReason;  // Reason for last status change
    private LocalDateTime createdAt;
}

