package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.service.ServiceCreateDTO;
import com.project.hostservice.dto.service.ServiceResponseDTO;
import com.project.hostservice.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping("/areas/{areaId}/services")
    public ResponseEntity<ApiResponse<List<ServiceResponseDTO>>> getServices(@PathVariable Long areaId) {
        log.info("GET /api/host/areas/{}/services", areaId);
        List<ServiceResponseDTO> result = serviceManagementService.getServicesByArea(areaId);
        log.info("GET /api/host/areas/{}/services - trả về {} dịch vụ", areaId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/areas/{areaId}/services")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> createService(@PathVariable Long areaId,
                                                                         @RequestBody ServiceCreateDTO request) {
        log.info("POST /api/host/areas/{}/services - serviceName: {}", areaId, request.getServiceName());
        ServiceResponseDTO result = serviceManagementService.createService(areaId, request);
        log.info("POST /api/host/areas/{}/services - tạo thành công serviceId: {}", areaId, result.getServiceId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/services/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> updateService(@PathVariable Long serviceId,
                                                                         @RequestBody ServiceCreateDTO request) {
        log.info("PUT /api/host/services/{}", serviceId);
        ServiceResponseDTO result = serviceManagementService.updateService(serviceId, request);
        log.info("PUT /api/host/services/{} - cập nhật thành công", serviceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long serviceId) {
        log.info("DELETE /api/host/services/{}", serviceId);
        serviceManagementService.deleteService(serviceId);
        log.info("DELETE /api/host/services/{} - xóa thành công", serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}