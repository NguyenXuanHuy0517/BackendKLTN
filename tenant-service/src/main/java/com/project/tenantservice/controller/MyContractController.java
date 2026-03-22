package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.service.MyContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tenant/contracts")
@RequiredArgsConstructor
public class MyContractController {

    private final MyContractService contractService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MyContractDTO>>> getMyContracts(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/contracts - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(contractService.getMyContracts(userId)));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<MyContractDTO>> getCurrentContract(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/contracts/current - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(contractService.getCurrentContract(userId)));
    }
}