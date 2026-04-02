package com.project.hostservice.service;

import com.project.datalayer.entity.MotelArea;
import com.project.datalayer.entity.Room;
import com.project.datalayer.entity.RoomStatusHistory;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.*;
import com.project.hostservice.dto.room.*;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến room.
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final MotelAreaRepository areaRepository;
    private final RoomStatusHistoryRepository statusHistoryRepository;
    private final ContractRepository contractRepository;
    private final RoomMapper roomMapper;

        /**
     * Chức năng: Lấy dữ liệu rooms by host.
     */
public List<RoomResponseDTO> getRoomsByHost(Long hostId) {
        return roomRepository.findByArea_Host_UserId(hostId).stream()
                .map(room -> {
                    RoomResponseDTO dto = roomMapper.toDTO(room);
                    contractRepository.findByRoom_RoomIdAndStatus(room.getRoomId(), "ACTIVE")
                            .ifPresent(contract -> {
                                dto.setCurrentTenantName(contract.getTenant().getFullName());
                                dto.setCurrentContractId(contract.getContractId());
                            });
                    return dto;
                })
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu rooms by area.
     */
public List<RoomResponseDTO> getRoomsByArea(Long areaId) {
        return roomRepository.findByArea_AreaId(areaId).stream()
                .map(room -> {
                    RoomResponseDTO dto = roomMapper.toDTO(room);
                    contractRepository.findByRoom_RoomIdAndStatus(room.getRoomId(), "ACTIVE")
                            .ifPresent(contract -> {
                                dto.setCurrentTenantName(contract.getTenant().getFullName());
                                dto.setCurrentContractId(contract.getContractId());
                            });
                    return dto;
                })
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu room detail.
     */
public RoomResponseDTO getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + roomId));
        RoomResponseDTO dto = roomMapper.toDTO(room);
        contractRepository.findByRoom_RoomIdAndStatus(roomId, "ACTIVE")
                .ifPresent(contract -> {
                    dto.setCurrentTenantName(contract.getTenant().getFullName());
                    dto.setCurrentContractId(contract.getContractId());
                });
        return dto;
    }

        /**
     * Chức năng: Tạo room.
     */
public RoomResponseDTO createRoom(RoomCreateDTO request) {
        MotelArea area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu trọ: " + request.getAreaId()));

        Room room = new Room();
        room.setArea(area);
        room.setRoomCode(request.getRoomCode());
        room.setFloor(request.getFloor());
        room.setBasePrice(request.getBasePrice());
        room.setElecPrice(request.getElecPrice());
        room.setWaterPrice(request.getWaterPrice());
        room.setAreaSize(request.getAreaSize());
        room.setAmenities(request.getAmenities());
        room.setImages(request.getImages());
        room.setDescription(request.getDescription());
        room.setStatus("AVAILABLE");

        roomRepository.save(room);
        return roomMapper.toDTO(room);
    }

        /**
     * Chức năng: Cập nhật room.
     */
public RoomResponseDTO updateRoom(Long roomId, RoomUpdateDTO request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + roomId));

        room.setBasePrice(request.getBasePrice());
        room.setElecPrice(request.getElecPrice());
        room.setWaterPrice(request.getWaterPrice());
        room.setAreaSize(request.getAreaSize());
        room.setAmenities(request.getAmenities());
        room.setImages(request.getImages());
        room.setDescription(request.getDescription());

        roomRepository.save(room);
        return roomMapper.toDTO(room);
    }

        /**
     * Chức năng: Cập nhật status.
     */
public void updateStatus(Long roomId, RoomStatusUpdateDTO request, User changedBy) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + roomId));

        String oldStatus = room.getStatus();
        room.setStatus(request.getStatus());
        roomRepository.save(room);

        RoomStatusHistory history = new RoomStatusHistory();
        history.setRoom(room);
        history.setOldStatus(oldStatus);
        history.setNewStatus(request.getStatus());
        history.setChangedBy(changedBy);
        history.setNote(request.getNote());
        statusHistoryRepository.save(history);
    }

        /**
     * Chức năng: Lấy dữ liệu status history.
     */
public List<RoomStatusHistory> getStatusHistory(Long roomId) {
        return statusHistoryRepository.findByRoom_RoomIdOrderByChangedAtDesc(roomId);
    }
}
