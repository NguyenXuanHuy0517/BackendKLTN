package com.project.hostservice.mapper;

import com.project.datalayer.entity.MotelArea;
import com.project.hostservice.dto.area.AreaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AreaMapper {

    public AreaResponseDTO toDTO(MotelArea area, int totalRooms, int availableRooms, int rentedRooms, int maintenanceRooms) {
        AreaResponseDTO dto = new AreaResponseDTO();
        dto.setAreaId(area.getAreaId());
        dto.setAreaName(area.getAreaName());
        dto.setAddress(area.getAddress());
        dto.setWard(area.getWard());
        dto.setDistrict(area.getDistrict());
        dto.setCity(area.getCity());
        dto.setLatitude(area.getLatitude() != null ? area.getLatitude().doubleValue() : null);
        dto.setLongitude(area.getLongitude() != null ? area.getLongitude().doubleValue() : null);
        dto.setDescription(area.getDescription());
        dto.setTotalRooms(totalRooms);
        dto.setAvailableRooms(availableRooms);
        dto.setRentedRooms(rentedRooms);
        dto.setMaintenanceRooms(maintenanceRooms);
        return dto;
    }
}