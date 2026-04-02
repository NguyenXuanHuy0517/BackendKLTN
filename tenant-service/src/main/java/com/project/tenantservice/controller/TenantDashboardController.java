package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.dashboard.TenantDashboardSummaryDTO;
import com.project.tenantservice.service.TenantDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tenant/dashboard")
@RequiredArgsConstructor
public class TenantDashboardController {

    private final TenantDashboardService tenantDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<TenantDashboardSummaryDTO>> getSummary(@RequestParam Long userId) {
        log.info("GET /api/tenant/dashboard/summary - userId: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(tenantDashboardService.getSummary(userId)));
    }
}
