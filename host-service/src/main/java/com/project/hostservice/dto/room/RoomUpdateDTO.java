package com.project.hostservice.dto.room;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomUpdateDTO {
    private BigDecimal basePrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private BigDecimal areaSize;
    private String amenities;
    private String images;
    private String description;
}