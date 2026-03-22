package com.project.hostservice.mapper;

import com.project.datalayer.entity.Deposit;
import com.project.hostservice.dto.deposit.DepositResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DepositMapper {

    public DepositResponseDTO toDTO(Deposit deposit) {
        DepositResponseDTO dto = new DepositResponseDTO();
        dto.setDepositId(deposit.getDepositId());
        dto.setTenantName(deposit.getTenant().getFullName());
        dto.setRoomCode(deposit.getRoom().getRoomCode());
        dto.setAmount(deposit.getAmount());
        dto.setExpectedCheckIn(deposit.getExpectedCheckIn());
        dto.setStatus(deposit.getStatus());
        dto.setNote(deposit.getNote());
        dto.setDepositDate(deposit.getDepositDate());
        return dto;
    }
}