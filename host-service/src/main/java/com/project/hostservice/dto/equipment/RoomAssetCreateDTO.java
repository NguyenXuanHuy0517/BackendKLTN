package com.project.hostservice.dto.equipment;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RoomAssetCreateDTO {
    private Long roomId;
    private Long equipmentId;
    private LocalDate assignedDate;
    private String note;
}