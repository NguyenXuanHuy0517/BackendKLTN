package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.datalayer.entity.RoomStatusHistory;
import com.project.datalayer.entity.User;
import com.project.hostservice.dto.room.*;
import com.project.hostservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ room và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

        /**
     * Chức năng: Lấy dữ liệu rooms.
     * URL: GET /api/host/rooms
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getRooms(@RequestParam Long hostId) {
        log.info("GET /api/host/rooms - hostId: {}", hostId);
        List<RoomResponseDTO> result = roomService.getRoomsByHost(hostId);
        log.info("GET /api/host/rooms - trả về {} phòng", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu rooms by area.
     * URL: GET /api/host/rooms/area/{areaId}
     */
@GetMapping("/area/{areaId}")
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getRoomsByArea(@PathVariable Long areaId) {
        log.info("GET /api/host/rooms/area/{}", areaId);
        List<RoomResponseDTO> result = roomService.getRoomsByArea(areaId);
        log.info("GET /api/host/rooms/area/{} - trả về {} phòng", areaId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu room detail.
     * URL: GET /api/host/rooms/{roomId}
     */
@GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> getRoomDetail(@PathVariable Long roomId) {
        log.info("GET /api/host/rooms/{}", roomId);
        RoomResponseDTO result = roomService.getRoomDetail(roomId);
        log.info("GET /api/host/rooms/{} - roomCode: {}", roomId, result.getRoomCode());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Tạo room.
     * URL: POST /api/host/rooms
     */
@PostMapping
    public ResponseEntity<ApiResponse<RoomResponseDTO>> createRoom(@RequestBody RoomCreateDTO request) {
        log.info("POST /api/host/rooms - areaId: {}, roomCode: {}", request.getAreaId(), request.getRoomCode());
        RoomResponseDTO result = roomService.createRoom(request);
        log.info("POST /api/host/rooms - tạo thành công roomId: {}", result.getRoomId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Cập nhật room.
     * URL: PUT /api/host/rooms/{roomId}
     */
@PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> updateRoom(@PathVariable Long roomId,
                                                                   @RequestBody RoomUpdateDTO request) {
        log.info("PUT /api/host/rooms/{}", roomId);
        RoomResponseDTO result = roomService.updateRoom(roomId, request);
        log.info("PUT /api/host/rooms/{} - cập nhật thành công", roomId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Cập nhật status.
     * URL: PATCH /api/host/rooms/{roomId}/status
     */
@PatchMapping("/{roomId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long roomId,
                                                          @RequestBody RoomStatusUpdateDTO request,
                                                          @RequestParam Long changedById) {
        log.info("PATCH /api/host/rooms/{}/status - status: {}", roomId, request.getStatus());
        User changedBy = new User();
        changedBy.setUserId(changedById);
        roomService.updateStatus(roomId, request, changedBy);
        log.info("PATCH /api/host/rooms/{}/status - đổi thành công sang {}", roomId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

        /**
     * Chức năng: Lấy dữ liệu status history.
     * URL: GET /api/host/rooms/{roomId}/history
     */
@GetMapping("/{roomId}/history")
    public ResponseEntity<ApiResponse<List<RoomStatusHistory>>> getStatusHistory(@PathVariable Long roomId) {
        log.info("GET /api/host/rooms/{}/history", roomId);
        List<RoomStatusHistory> result = roomService.getStatusHistory(roomId);
        log.info("GET /api/host/rooms/{}/history - trả về {} bản ghi", roomId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
