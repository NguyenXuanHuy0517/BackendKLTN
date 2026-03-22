package com.project.hostservice.dto.area;

import lombok.Data;

@Data
public class AreaResponseDTO {
    private Long areaId;
    private String areaName;
    private String address;
    private String ward;
    private String district;
    private String city;
    private Double latitude;
    private Double longitude;
    private String description;
    private int totalRooms;
    private int availableRooms;
    private int rentedRooms;
    private int maintenanceRooms;
}