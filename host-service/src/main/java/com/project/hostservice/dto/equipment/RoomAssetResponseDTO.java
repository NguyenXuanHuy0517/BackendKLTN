package com.project.hostservice.dto.equipment;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RoomAssetResponseDTO {
    private Long equipmentId;
    private String equipmentName;
    private String serialNumber;
    private String status;
    private LocalDate assignedDate;
    private String note;
}