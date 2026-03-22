package com.project.tenantservice.mapper;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.ContractService;
import com.project.tenantservice.dto.contract.MyContractDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ContractMapper {

    public MyContractDTO toDTO(Contract contract, List<ContractService> services) {
        MyContractDTO dto = new MyContractDTO();
        dto.setContractId(contract.getContractId());
        dto.setContractCode(contract.getContractCode());
        dto.setRoomCode(contract.getRoom().getRoomCode());
        dto.setAreaName(contract.getRoom().getArea().getAreaName());
        dto.setAreaAddress(contract.getRoom().getArea().getAddress());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setActualRentPrice(contract.getActualRentPrice());
        dto.setStatus(contract.getStatus());

        // Ưu tiên giá override, fallback về giá phòng
        dto.setElecPrice(contract.getElecPriceOverride() != null
                ? contract.getElecPriceOverride()
                : contract.getRoom().getElecPrice());
        dto.setWaterPrice(contract.getWaterPriceOverride() != null
                ? contract.getWaterPriceOverride()
                : contract.getRoom().getWaterPrice());

        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());

        long days = LocalDate.now().until(contract.getEndDate()).getDays();
        dto.setDaysUntilExpiry(days);

        return dto;
    }
}