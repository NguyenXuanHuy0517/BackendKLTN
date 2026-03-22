package com.project.hostservice.mapper;

import com.project.datalayer.entity.RoomAsset;
import com.project.hostservice.dto.equipment.RoomAssetResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RoomAssetMapper {

    public RoomAssetResponseDTO toDTO(RoomAsset roomAsset) {
        RoomAssetResponseDTO dto = new RoomAssetResponseDTO();
        dto.setEquipmentId(roomAsset.getEquipment().getEquipmentId());
        dto.setEquipmentName(roomAsset.getEquipment().getName());
        dto.setSerialNumber(roomAsset.getEquipment().getSerialNumber());
        dto.setStatus(roomAsset.getEquipment().getStatus());
        dto.setAssignedDate(roomAsset.getAssignedDate());
        dto.setNote(roomAsset.getNote());
        return dto;
    }
}