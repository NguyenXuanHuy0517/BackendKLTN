package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.service.AddServiceToContractDTO;
import com.project.tenantservice.dto.service.TenantServiceDTO;
import com.project.tenantservice.dto.service.UpdateServiceQuantityDTO;
import com.project.tenantservice.service.MyServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tenant/services")
@RequiredArgsConstructor
public class MyServiceController {

    private final MyServiceService myServiceService;

    @GetMapping("/available/{contractId}")
    public ResponseEntity<ApiResponse<List<TenantServiceDTO>>> getAvailableServices(@PathVariable Long contractId) {
        log.info("GET /api/tenant/services/available/{}", contractId);
        List<TenantServiceDTO> services = myServiceService.getAvailableServices(contractId);
        log.info("GET /api/tenant/services/available/{} - trả về {} dịch vụ", contractId, services.size());
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ApiResponse<List<TenantServiceDTO>>> getContractServices(@PathVariable Long contractId) {
        log.info("GET /api/tenant/services/{}", contractId);
        List<TenantServiceDTO> services = myServiceService.getContractServices(contractId);
        log.info("GET /api/tenant/services/{} - trả về {} dịch vụ", contractId, services.size());
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addServiceToContract(
            @RequestParam Long userId,
            @Valid @RequestBody AddServiceToContractDTO request) {
        log.info("POST /api/tenant/services/add - userId: {}, contractId: {}, serviceId: {}",
                 userId, request.getContractId(), request.getServiceId());

        myServiceService.addServiceToContract(userId, request);

        log.info("POST /api/tenant/services/add - thêm dịch vụ thành công");
        return ResponseEntity.ok(ApiResponse.success(null, "Thêm dịch vụ thành công"));
    }

    @DeleteMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> removeServiceFromContract(
            @RequestParam Long userId,
            @PathVariable Long contractId,
            @PathVariable Long serviceId) {
        log.info("DELETE /api/tenant/services/{}/services/{} - userId: {}", contractId, serviceId, userId);

        myServiceService.removeServiceFromContract(userId, contractId, serviceId);

        log.info("DELETE /api/tenant/services/{}/services/{} - gỡ dịch vụ thành công", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Gỡ dịch vụ thành công"));
    }

    @PatchMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> updateServiceQuantity(
            @RequestParam Long userId,
            @PathVariable Long contractId,
            @PathVariable Long serviceId,
            @Valid @RequestBody UpdateServiceQuantityDTO request) {
        log.info("PATCH /api/tenant/services/{}/services/{} - userId: {}, quantity: {}",
                 contractId, serviceId, userId, request.getQuantity());

        myServiceService.updateServiceQuantity(userId, contractId, serviceId, request);

        log.info("PATCH /api/tenant/services/{}/services/{} - cập nhật thành công", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật dịch vụ thành công"));
    }
}

