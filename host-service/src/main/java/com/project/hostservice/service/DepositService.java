package com.project.hostservice.service;

import com.project.datalayer.entity.Deposit;
import com.project.datalayer.entity.Room;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.DepositRepository;
import com.project.datalayer.repository.RoomRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.deposit.DepositCreateDTO;
import com.project.hostservice.dto.deposit.DepositResponseDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.DepositMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final DepositMapper depositMapper;

    public List<DepositResponseDTO> getDepositsByHost(Long hostId) {
        return depositRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .map(depositMapper::toDTO)
                .toList();
    }

    public DepositResponseDTO createDeposit(DepositCreateDTO request) {
        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người thuê: " + request.getTenantId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + request.getRoomId()));

        Deposit deposit = new Deposit();
        deposit.setTenant(tenant);
        deposit.setRoom(room);
        deposit.setAmount(request.getAmount());
        deposit.setExpectedCheckIn(request.getExpectedCheckIn());
        deposit.setNote(request.getNote());
        deposit.setStatus("PENDING");

        room.setStatus("DEPOSITED");
        roomRepository.save(room);

        depositRepository.save(deposit);
        return depositMapper.toDTO(deposit);
    }

    public void confirmDeposit(Long depositId, User confirmedBy) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt cọc: " + depositId));
        deposit.setStatus("CONFIRMED");
        deposit.setConfirmedBy(confirmedBy);
        deposit.setConfirmedAt(java.time.LocalDateTime.now());
        depositRepository.save(deposit);
    }

    public void refundDeposit(Long depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt cọc: " + depositId));
        deposit.setStatus("REFUNDED");

        Room room = deposit.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        depositRepository.save(deposit);
    }
}