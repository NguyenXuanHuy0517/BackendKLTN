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

/**
 * Vai trò: REST controller của module admin-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ admin dashboard và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

        /**
     * Chức năng: Lấy dữ liệu dashboard.
     * URL: GET /api/admin/dashboard
     */
@GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        log.info("GET /api/admin/dashboard");
        AdminDashboardDTO dashboard = dashboardService.getDashboard();
        log.info("GET /api/admin/dashboard - trả về dashboard");
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
