package com.project.hostservice.dto.equipment;

import lombok.Data;

@Data
public class EquipmentDTO {
    private Long equipmentId;
    private String name;
    private String serialNumber;
    private String status;
    private String note;
}