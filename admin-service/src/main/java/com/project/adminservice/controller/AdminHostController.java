package com.project.adminservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.adminservice.dto.host.AdminHostResponseDTO;
import com.project.adminservice.service.AdminHostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/hosts")
@RequiredArgsConstructor
public class AdminHostController {

    private final AdminHostService hostService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminHostResponseDTO>>> getAllHosts() {
        log.info("GET /api/admin/hosts");
        List<AdminHostResponseDTO> hosts = hostService.getAllHosts();
        log.info("GET /api/admin/hosts - trả về {} host", hosts.size());
        return ResponseEntity.ok(ApiResponse.success(hosts));
    }

    @GetMapping("/{hostId}")
    public ResponseEntity<ApiResponse<AdminHostResponseDTO>> getHostDetail(@PathVariable Long hostId) {
        log.info("GET /api/admin/hosts/{}", hostId);
        AdminHostResponseDTO host = hostService.getHostDetail(hostId);
        log.info("GET /api/admin/hosts/{} - trả về chi tiết host", hostId);
        return ResponseEntity.ok(ApiResponse.success(host));
    }

    @PatchMapping("/{hostId}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleHostActive(@PathVariable Long hostId) {
        log.info("PATCH /api/admin/hosts/{}/toggle", hostId);
        hostService.toggleActive(hostId);
        log.info("PATCH /api/admin/hosts/{}/toggle - thành công", hostId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

