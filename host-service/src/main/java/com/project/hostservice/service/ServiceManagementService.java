package com.project.hostservice.service;

import com.project.datalayer.entity.MotelArea;
import com.project.datalayer.entity.Service;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.MotelAreaRepository;
import com.project.datalayer.repository.ServiceRepository;
import com.project.hostservice.dto.service.ServiceCreateDTO;
import com.project.hostservice.dto.service.ServiceResponseDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final MotelAreaRepository areaRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ServiceMapper serviceMapper;

    public List<ServiceResponseDTO> getServicesByArea(Long areaId) {
        return serviceRepository.findByArea_AreaId(areaId).stream()
                .map(service -> {
                    int usageCount = contractServiceRepository.findAll().stream()
                            .filter(cs -> cs.getService().getServiceId().equals(service.getServiceId()))
                            .toList().size();
                    return serviceMapper.toDTO(service, usageCount);
                })
                .toList();
    }

    public ServiceResponseDTO createService(Long areaId, ServiceCreateDTO request) {
        MotelArea area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu trọ: " + areaId));

        Service service = new Service();
        service.setArea(area);
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnitName(request.getUnitName());
        service.setDescription(request.getDescription());
        service.setActive(true);

        serviceRepository.save(service);
        return serviceMapper.toDTO(service, 0);
    }

    public ServiceResponseDTO updateService(Long serviceId, ServiceCreateDTO request) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ: " + serviceId));

        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnitName(request.getUnitName());
        service.setDescription(request.getDescription());

        serviceRepository.save(service);
        return serviceMapper.toDTO(service, 0);
    }

    public void deleteService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ: " + serviceId));
        service.setActive(false);
        serviceRepository.save(service);
    }
}