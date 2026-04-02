package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.dto.contract.RentalJoinClaimRequestDTO;
import com.project.tenantservice.dto.contract.RentalJoinPreviewDTO;
import com.project.tenantservice.service.RentalJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tenant/rental-join")
@RequiredArgsConstructor
public class RentalJoinController {

    private final RentalJoinService rentalJoinService;

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<RentalJoinPreviewDTO>> previewInvitation(
            @RequestBody RentalJoinClaimRequestDTO request,
            Authentication authentication
    ) {
        log.info("POST /api/tenant/rental-join/preview");
        return ResponseEntity.ok(ApiResponse.success(
                rentalJoinService.previewInvitation(request.getInviteCode(), authentication)
        ));
    }

    @PostMapping("/claim")
    public ResponseEntity<ApiResponse<MyContractDTO>> claimInvitation(
            @RequestBody RentalJoinClaimRequestDTO request,
            Authentication authentication
    ) {
        log.info("POST /api/tenant/rental-join/claim");
        return ResponseEntity.ok(ApiResponse.success(
                rentalJoinService.claimInvitation(request.getInviteCode(), authentication)
        ));
    }
}
