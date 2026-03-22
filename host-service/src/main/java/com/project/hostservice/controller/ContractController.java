package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.datalayer.entity.User;
import com.project.hostservice.dto.contract.ContractCreateDTO;
import com.project.hostservice.dto.contract.ContractExtendDTO;
import com.project.hostservice.dto.contract.ContractResponseDTO;
import com.project.hostservice.service.ContractManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractManagementService contractService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponseDTO>>> getContracts(@RequestParam Long hostId) {
        log.info("GET /api/host/contracts - hostId: {}", hostId);
        List<ContractResponseDTO> result = contractService.getContractsByHost(hostId);
        log.info("GET /api/host/contracts - trả về {} hợp đồng", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> getContractDetail(@PathVariable Long contractId) {
        log.info("GET /api/host/contracts/{}", contractId);
        ContractResponseDTO result = contractService.getContractDetail(contractId);
        log.info("GET /api/host/contracts/{} - contractCode: {}", contractId, result.getContractCode());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContractResponseDTO>> createContract(@RequestBody ContractCreateDTO request) {
        log.info("POST /api/host/contracts - tenantId: {}, roomId: {}", request.getTenantId(), request.getRoomId());
        ContractResponseDTO result = contractService.createContract(request);
        log.info("POST /api/host/contracts - tạo thành công contractId: {}", result.getContractId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{contractId}/extend")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> extendContract(@PathVariable Long contractId,
                                                                           @RequestBody ContractExtendDTO request) {
        log.info("PUT /api/host/contracts/{}/extend - newEndDate: {}", contractId, request.getNewEndDate());
        ContractResponseDTO result = contractService.extendContract(contractId, request);
        log.info("PUT /api/host/contracts/{}/extend - gia hạn thành công", contractId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{contractId}/terminate")
    public ResponseEntity<ApiResponse<Void>> terminateContract(@PathVariable Long contractId,
                                                               @RequestParam Long terminatedById) {
        log.info("PATCH /api/host/contracts/{}/terminate - terminatedById: {}", contractId, terminatedById);
        User terminatedBy = new User();
        terminatedBy.setUserId(terminatedById);
        contractService.terminateContract(contractId, terminatedBy);
        log.info("PATCH /api/host/contracts/{}/terminate - chấm dứt thành công", contractId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> addService(@PathVariable Long contractId,
                                                        @PathVariable Long serviceId) {
        log.info("POST /api/host/contracts/{}/services/{}", contractId, serviceId);
        contractService.addService(contractId, serviceId);
        log.info("POST /api/host/contracts/{}/services/{} - thêm dịch vụ thành công", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> removeService(@PathVariable Long contractId,
                                                           @PathVariable Long serviceId) {
        log.info("DELETE /api/host/contracts/{}/services/{}", contractId, serviceId);
        contractService.removeService(contractId, serviceId);
        log.info("DELETE /api/host/contracts/{}/services/{} - xóa dịch vụ thành công", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}