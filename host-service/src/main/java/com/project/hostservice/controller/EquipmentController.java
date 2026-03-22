package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.equipment.EquipmentDTO;
import com.project.hostservice.dto.equipment.RoomAssetCreateDTO;
import com.project.hostservice.dto.equipment.RoomAssetResponseDTO;
import com.project.hostservice.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    public ResponseEntity<ApiResponse<List<EquipmentDTO>>> getAllEquipments() {
        log.info("GET /api/host/equipments");
        List<EquipmentDTO> result = equipmentService.getAllEquipments();
        log.info("GET /api/host/equipments - trả về {} thiết bị", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/rooms/{roomId}/assets")
    public ResponseEntity<ApiResponse<List<RoomAssetResponseDTO>>> getAssetsByRoom(@PathVariable Long roomId) {
        log.info("GET /api/host/rooms/{}/assets", roomId);
        List<RoomAssetResponseDTO> result = equipmentService.getAssetsByRoom(roomId);
        log.info("GET /api/host/rooms/{}/assets - trả về {} thiết bị", roomId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/rooms/{roomId}/assets")
    public ResponseEntity<ApiResponse<RoomAssetResponseDTO>> addAsset(@PathVariable Long roomId,
                                                                      @RequestBody RoomAssetCreateDTO request) {
        log.info("POST /api/host/rooms/{}/assets - equipmentId: {}", roomId, request.getEquipmentId());
        request.setRoomId(roomId);
        RoomAssetResponseDTO result = equipmentService.addAssetToRoom(request);
        log.info("POST /api/host/rooms/{}/assets - gán thành công", roomId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/rooms/{roomId}/assets/{equipmentId}")
    public ResponseEntity<ApiResponse<Void>> removeAsset(@PathVariable Long roomId,
                                                         @PathVariable Long equipmentId) {
        log.info("DELETE /api/host/rooms/{}/assets/{}", roomId, equipmentId);
        equipmentService.removeAssetFromRoom(roomId, equipmentId);
        log.info("DELETE /api/host/rooms/{}/assets/{} - gỡ thành công", roomId, equipmentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}