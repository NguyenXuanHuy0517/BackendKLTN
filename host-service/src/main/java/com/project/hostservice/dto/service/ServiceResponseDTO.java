package com.project.hostservice.dto.service;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceResponseDTO {
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private String unitName;
    private String description;
    private boolean active;
    private int usageCount;
}