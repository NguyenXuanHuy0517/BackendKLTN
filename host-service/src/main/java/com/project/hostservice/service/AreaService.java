package com.project.hostservice.service;

import com.project.datalayer.entity.MotelArea;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.MotelAreaRepository;
import com.project.datalayer.repository.RoomRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.area.AreaCreateDTO;
import com.project.hostservice.dto.area.AreaResponseDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.AreaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final MotelAreaRepository areaRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AreaMapper areaMapper;

    public List<AreaResponseDTO> getAreasByHost(Long hostId) {
        return areaRepository.findByHost_UserId(hostId).stream()
                .map(area -> areaMapper.toDTO(
                        area,
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "RENTED") +
                                roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "AVAILABLE") +
                                roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "MAINTENANCE") +
                                roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "DEPOSITED"),
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "AVAILABLE"),
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "RENTED"),
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "MAINTENANCE")
                ))
                .toList();
    }

    public AreaResponseDTO createArea(Long hostId, AreaCreateDTO request) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy host: " + hostId));

        MotelArea area = new MotelArea();
        area.setHost(host);
        area.setAreaName(request.getAreaName());
        area.setAddress(request.getAddress());
        area.setWard(request.getWard());
        area.setDistrict(request.getDistrict());
        area.setCity(request.getCity());
        area.setLatitude(request.getLatitude() != null ? BigDecimal.valueOf(request.getLatitude()) : null);
        area.setLongitude(request.getLongitude() != null ? BigDecimal.valueOf(request.getLongitude()) : null);
        area.setDescription(request.getDescription());

        areaRepository.save(area);
        return areaMapper.toDTO(area, 0, 0, 0, 0);
    }

    public AreaResponseDTO updateArea(Long areaId, AreaCreateDTO request) {
        MotelArea area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu trọ: " + areaId));

        area.setAreaName(request.getAreaName());
        area.setAddress(request.getAddress());
        area.setWard(request.getWard());
        area.setDistrict(request.getDistrict());
        area.setCity(request.getCity());
        area.setLatitude(request.getLatitude() != null ? BigDecimal.valueOf(request.getLatitude()) : null);
        area.setLongitude(request.getLongitude() != null ? BigDecimal.valueOf(request.getLongitude()) : null);
        area.setDescription(request.getDescription());

        areaRepository.save(area);
        return areaMapper.toDTO(
                area,
                roomRepository.countByArea_AreaIdAndStatus(areaId, "RENTED") +
                        roomRepository.countByArea_AreaIdAndStatus(areaId, "AVAILABLE") +
                        roomRepository.countByArea_AreaIdAndStatus(areaId, "MAINTENANCE") +
                        roomRepository.countByArea_AreaIdAndStatus(areaId, "DEPOSITED"),
                roomRepository.countByArea_AreaIdAndStatus(areaId, "AVAILABLE"),
                roomRepository.countByArea_AreaIdAndStatus(areaId, "RENTED"),
                roomRepository.countByArea_AreaIdAndStatus(areaId, "MAINTENANCE")
        );
    }
}