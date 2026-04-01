package com.project.adminservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.adminservice.dto.dashboard.AdminDashboardDTO;
import com.project.adminservice.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        log.info("GET /api/admin/dashboard");
        AdminDashboardDTO dashboard = dashboardService.getDashboard();
        log.info("GET /api/admin/dashboard - trả về dashboard");
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
