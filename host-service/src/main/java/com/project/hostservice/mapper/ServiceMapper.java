package com.project.hostservice.mapper;

import com.project.datalayer.entity.Service;
import com.project.hostservice.dto.service.ServiceResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceResponseDTO toDTO(Service service, int usageCount) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setServiceId(service.getServiceId());
        dto.setServiceName(service.getServiceName());
        dto.setPrice(service.getPrice());
        dto.setUnitName(service.getUnitName());
        dto.setDescription(service.getDescription());
        dto.setActive(service.isActive());
        dto.setUsageCount(usageCount);
        return dto;
    }
}