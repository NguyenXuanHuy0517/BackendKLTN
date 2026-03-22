package com.project.hostservice.dto.room;

import lombok.Data;
import java.math.BigDecimal;

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
    private String images;
    private String description;
    private Long areaId;
    private String areaName;
    private String currentTenantName;
    private Long currentContractId;
}