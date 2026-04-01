package com.project.adminservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.adminservice.dto.room.AdminRoomAuditDTO;
import com.project.adminservice.service.AdminRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final AdminRoomService roomService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminRoomAuditDTO>>> getAllRooms() {
        log.info("GET /api/admin/rooms");
        List<AdminRoomAuditDTO> rooms = roomService.getAllRooms();
        log.info("GET /api/admin/rooms - trả về {} phòng", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/without-invoice")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminRoomAuditDTO>>> getRoomsWithoutInvoice() {
        log.info("GET /api/admin/rooms/without-invoice");
        List<AdminRoomAuditDTO> rooms = roomService.getRoomsMissingInvoices();
        log.info("GET /api/admin/rooms/without-invoice - trả về {} phòng", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}