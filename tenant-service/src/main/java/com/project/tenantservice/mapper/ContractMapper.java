package com.project.tenantservice.mapper;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.ContractService;
import com.project.hostservice.dto.contract.ContractServiceDTO;
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

        // NEW - Map full ContractService details
        dto.setContractServices(services.stream()
                .map(this::toContractServiceDTO)
                .toList());

        // Keep for backward compatibility
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());

        long days = LocalDate.now().until(contract.getEndDate()).getDays();
        dto.setDaysUntilExpiry(days);

        return dto;
    }

    // NEW - Helper method to map ContractService to ContractServiceDTO
    private ContractServiceDTO toContractServiceDTO(ContractService contractService) {
        ContractServiceDTO dto = new ContractServiceDTO();
        dto.setContractServiceId(contractService.getId().getServiceId());
        dto.setServiceId(contractService.getService().getServiceId());
        dto.setServiceName(contractService.getService().getServiceName());
        dto.setQuantity(contractService.getQuantity());
        dto.setPrice(contractService.getPriceSnapshot());
        dto.setUnitName(contractService.getService().getUnitName());
        dto.setCurrentServicePrice(contractService.getService().getPrice());
        return dto;
    }
}