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

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ contract và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractManagementService contractService;

        /**
     * Chức năng: Lấy dữ liệu contracts.
     * URL: GET /api/host/contracts
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponseDTO>>> getContracts(@RequestParam Long hostId) {
        log.info("GET /api/host/contracts - hostId: {}", hostId);
        List<ContractResponseDTO> result = contractService.getContractsByHost(hostId);
        log.info("GET /api/host/contracts - trả về {} hợp đồng", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu contract detail.
     * URL: GET /api/host/contracts/{contractId}
     */
@GetMapping("/{contractId}")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> getContractDetail(@PathVariable Long contractId) {
        log.info("GET /api/host/contracts/{}", contractId);
        ContractResponseDTO result = contractService.getContractDetail(contractId);
        log.info("GET /api/host/contracts/{} - contractCode: {}", contractId, result.getContractCode());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Tạo contract.
     * URL: POST /api/host/contracts
     */
@PostMapping
    public ResponseEntity<ApiResponse<ContractResponseDTO>> createContract(@RequestBody ContractCreateDTO request) {
        log.info("POST /api/host/contracts - tenantId: {}, roomId: {}", request.getTenantId(), request.getRoomId());
        ContractResponseDTO result = contractService.createContract(request);
        log.info("POST /api/host/contracts - tạo thành công contractId: {}", result.getContractId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ extend contract.
     * URL: PUT /api/host/contracts/{contractId}/extend
     */
@PutMapping("/{contractId}/extend")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> extendContract(@PathVariable Long contractId,
                                                                           @RequestBody ContractExtendDTO request) {
        log.info("PUT /api/host/contracts/{}/extend - newEndDate: {}", contractId, request.getNewEndDate());
        ContractResponseDTO result = contractService.extendContract(contractId, request);
        log.info("PUT /api/host/contracts/{}/extend - gia hạn thành công", contractId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ terminate contract.
     * URL: PATCH /api/host/contracts/{contractId}/terminate
     */
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

        /**
     * Chức năng: Thêm service.
     * URL: POST /api/host/contracts/{contractId}/services/{serviceId}
     */
@PostMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> addService(@PathVariable Long contractId,
                                                        @PathVariable Long serviceId) {
        log.info("POST /api/host/contracts/{}/services/{}", contractId, serviceId);
        contractService.addService(contractId, serviceId);
        log.info("POST /api/host/contracts/{}/services/{} - thêm dịch vụ thành công", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

        /**
     * Chức năng: Loại bỏ service.
     * URL: DELETE /api/host/contracts/{contractId}/services/{serviceId}
     */
@DeleteMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> removeService(@PathVariable Long contractId,
                                                           @PathVariable Long serviceId) {
        log.info("DELETE /api/host/contracts/{}/services/{}", contractId, serviceId);
        contractService.removeService(contractId, serviceId);
        log.info("DELETE /api/host/contracts/{}/services/{} - xóa dịch vụ thành công", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
