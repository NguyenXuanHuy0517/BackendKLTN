package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.report.ReportDTO;
import com.project.hostservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/host/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ReportDTO>> getDashboard(@RequestParam Long hostId) {
        log.info("GET /api/host/reports/dashboard - hostId: {}", hostId);
        ReportDTO result = reportService.getDashboard(hostId);
        log.info("GET /api/host/reports/dashboard - revenue: {}, occupancy: {}%",
                result.getTotalRevenue(), result.getOccupancyRate());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}