package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.datalayer.entity.User;
import com.project.hostservice.dto.deposit.DepositCreateDTO;
import com.project.hostservice.dto.deposit.DepositResponseDTO;
import com.project.hostservice.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepositResponseDTO>>> getDeposits(@RequestParam Long hostId) {
        log.info("GET /api/host/deposits - hostId: {}", hostId);
        List<DepositResponseDTO> result = depositService.getDepositsByHost(hostId);
        log.info("GET /api/host/deposits - trả về {} đặt cọc", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepositResponseDTO>> createDeposit(@RequestBody DepositCreateDTO request) {
        log.info("POST /api/host/deposits - tenantId: {}, roomId: {}", request.getTenantId(), request.getRoomId());
        DepositResponseDTO result = depositService.createDeposit(request);
        log.info("POST /api/host/deposits - tạo thành công depositId: {}", result.getDepositId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{depositId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmDeposit(@PathVariable Long depositId,
                                                            @RequestParam Long confirmedById) {
        log.info("PATCH /api/host/deposits/{}/confirm - confirmedById: {}", depositId, confirmedById);
        User confirmedBy = new User();
        confirmedBy.setUserId(confirmedById);
        depositService.confirmDeposit(depositId, confirmedBy);
        log.info("PATCH /api/host/deposits/{}/confirm - xác nhận thành công", depositId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{depositId}/refund")
    public ResponseEntity<ApiResponse<Void>> refundDeposit(@PathVariable Long depositId) {
        log.info("PATCH /api/host/deposits/{}/refund", depositId);
        depositService.refundDeposit(depositId);
        log.info("PATCH /api/host/deposits/{}/refund - hoàn cọc thành công", depositId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}