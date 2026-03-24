package com.project.hostservice.mapper;

import com.project.datalayer.entity.Room;
import com.project.hostservice.dto.room.RoomResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        
        // Parse images JSON to list
        List<String> imagesList = parseJsonImages(room.getImages());
        dto.setImagesList(imagesList);
        
        dto.setDescription(room.getDescription());
        dto.setAreaId(room.getArea().getAreaId());
        dto.setAreaName(room.getArea().getAreaName());
        
        // Load host information
        if (room.getArea().getHost() != null) {
            dto.setHostName(room.getArea().getHost().getFullName());
            dto.setHostAvatar(room.getArea().getHost().getAvatarUrl());
        }
        
        return dto;
    }

    private List<String> parseJsonImages(String imagesJson) {
        List<String> result = new ArrayList<>();
        
        if (imagesJson == null || imagesJson.trim().isEmpty() || imagesJson.equals("[]")) {
            return result;
        }
        
        try {
            // Simple JSON parsing without ObjectMapper
            // Expected format: ["url1", "url2", ...]
            String cleaned = imagesJson.trim();
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            
            if (!cleaned.isEmpty()) {
                String[] urls = cleaned.split(",");
                for (String url : urls) {
                    String trimmed = url.trim();
                    // Remove quotes if present
                    if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                        trimmed = trimmed.substring(1, trimmed.length() - 1);
                    }
                    if (!trimmed.isEmpty()) {
                        result.add(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            // Return empty list if parsing fails
        }
        
        return result;
    }
}