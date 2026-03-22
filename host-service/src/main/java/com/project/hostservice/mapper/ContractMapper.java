package com.project.hostservice.mapper;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.ContractService;
import com.project.hostservice.dto.contract.ContractResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractMapper {

    public ContractResponseDTO toDTO(Contract contract, List<ContractService> services) {
        ContractResponseDTO dto = new ContractResponseDTO();
        dto.setContractId(contract.getContractId());
        dto.setContractCode(contract.getContractCode());
        dto.setTenantName(contract.getTenant().getFullName());
        dto.setRoomCode(contract.getRoom().getRoomCode());
        dto.setAreaName(contract.getRoom().getArea().getAreaName());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setActualRentPrice(contract.getActualRentPrice());
        dto.setElecPriceOverride(contract.getElecPriceOverride());
        dto.setWaterPriceOverride(contract.getWaterPriceOverride());
        dto.setStatus(contract.getStatus());
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());
        return dto;
    }
}