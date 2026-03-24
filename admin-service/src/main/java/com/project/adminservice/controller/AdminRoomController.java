package com.project.adminservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.adminservice.dto.room.AdminRoomResponseDTO;
import com.project.adminservice.service.AdminRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final AdminRoomService roomService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRoomResponseDTO>>> getAllRooms() {
        log.info("GET /api/admin/rooms");
        List<AdminRoomResponseDTO> rooms = roomService.getAllRooms();
        log.info("GET /api/admin/rooms - trả về {} phòng", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/missing-invoices")
    public ResponseEntity<ApiResponse<List<AdminRoomResponseDTO>>> getRoomsMissingInvoices() {
        log.info("GET /api/admin/rooms/missing-invoices");
        List<AdminRoomResponseDTO> rooms = roomService.getRoomsMissingInvoices();
        log.info("GET /api/admin/rooms/missing-invoices - trả về {} phòng", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}

