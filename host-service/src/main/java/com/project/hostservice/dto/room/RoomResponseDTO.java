package com.project.hostservice.dto.room;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomResponseDTO {
    private Long roomId;
    private String roomCode;
    private Integer floor;
    private BigDecimal basePrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private BigDecimal areaSize;
    private String status;
    private String amenities;
    private String images;  // JSON raw data
    private List<String> imagesList;  // Parsed list of image URLs
    private String description;
    private Long areaId;
    private String areaName;
    private String hostName;
    private String hostAvatar;  // Avatar URL of the host/owner
    private String currentTenantName;
    private Long currentContractId;
}