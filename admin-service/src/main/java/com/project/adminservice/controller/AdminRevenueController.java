package com.project.adminservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.adminservice.dto.revenue.AdminRevenueDTO;
import com.project.adminservice.service.AdminRevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class AdminRevenueController {

    private final AdminRevenueService revenueService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminRevenueDTO>> getRevenue(
            @RequestParam(defaultValue = "month") String period) {
        log.info("GET /api/admin/revenue?period={}", period);
        AdminRevenueDTO revenue = revenueService.getRevenue(period);
        log.info("GET /api/admin/revenue?period={} - trả về revenue", period);
        return ResponseEntity.ok(ApiResponse.success(revenue));
    }
}
