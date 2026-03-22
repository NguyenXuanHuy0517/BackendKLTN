package com.project.hostservice.dto.area;

import lombok.Data;

@Data
public class AreaCreateDTO {
    private String areaName;
    private String address;
    private String ward;
    private String district;
    private String city;
    private Double latitude;
    private Double longitude;
    private String description;
}