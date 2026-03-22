package com.project.hostservice.mapper;

import com.project.datalayer.entity.Room;
import com.project.hostservice.dto.room.RoomResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponseDTO toDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomCode(room.getRoomCode());
        dto.setFloor(room.getFloor());
        dto.setBasePrice(room.getBasePrice());
        dto.setElecPrice(room.getElecPrice());
        dto.setWaterPrice(room.getWaterPrice());
        dto.setAreaSize(room.getAreaSize());
        dto.setStatus(room.getStatus());
        dto.setAmenities(room.getAmenities());
        dto.setImages(room.getImages());
        dto.setDescription(room.getDescription());
        dto.setAreaId(room.getArea().getAreaId());
        dto.setAreaName(room.getArea().getAreaName());
        return dto;
    }
}