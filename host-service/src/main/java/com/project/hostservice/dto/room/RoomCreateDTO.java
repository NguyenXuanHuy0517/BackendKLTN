package com.project.hostservice.dto.room;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomCreateDTO {
    private Long areaId;
    private String roomCode;
    private Integer floor;
    private BigDecimal basePrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private BigDecimal areaSize;
    private String amenities;
    private String images;
    private String description;
}