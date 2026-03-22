package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.profile.ProfileResponseDTO;
import com.project.tenantservice.dto.profile.ProfileUpdateDTO;
import com.project.tenantservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tenant/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfile(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/profile - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(profileService.getProfile(userId)));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> updateProfile(
            @RequestParam Long userId,
            @RequestBody ProfileUpdateDTO request) {
        log.info("PUT /api/tenant/profile - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(profileService.updateProfile(userId, request)));
    }
}