package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.area.AreaCreateDTO;
import com.project.hostservice.dto.area.AreaResponseDTO;
import com.project.hostservice.service.AreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AreaResponseDTO>>> getAreas(@RequestParam Long hostId) {
        log.info("GET /api/host/areas - hostId: {}", hostId);
        List<AreaResponseDTO> result = areaService.getAreasByHost(hostId);
        log.info("GET /api/host/areas - trả về {} khu trọ", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AreaResponseDTO>> createArea(@RequestParam Long hostId,
                                                                   @RequestBody AreaCreateDTO request) {
        log.info("POST /api/host/areas - hostId: {}, areaName: {}", hostId, request.getAreaName());
        AreaResponseDTO result = areaService.createArea(hostId, request);
        log.info("POST /api/host/areas - tạo thành công areaId: {}", result.getAreaId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{areaId}")
    public ResponseEntity<ApiResponse<AreaResponseDTO>> updateArea(@PathVariable Long areaId,
                                                                   @RequestBody AreaCreateDTO request) {
        log.info("PUT /api/host/areas/{} - areaName: {}", areaId, request.getAreaName());
        AreaResponseDTO result = areaService.updateArea(areaId, request);
        log.info("PUT /api/host/areas/{} - cập nhật thành công", areaId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}