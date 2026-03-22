package com.project.hostservice.service;

import com.project.datalayer.entity.*;
import com.project.datalayer.repository.*;
import com.project.hostservice.dto.contract.ContractCreateDTO;
import com.project.hostservice.dto.contract.ContractExtendDTO;
import com.project.hostservice.dto.contract.ContractResponseDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractManagementService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final DepositRepository depositRepository;
    private final ServiceRepository serviceRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ContractMapper contractMapper;

    public List<ContractResponseDTO> getContractsByHost(Long hostId) {
        return contractRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .map(contract -> contractMapper.toDTO(
                        contract,
                        contractServiceRepository.findByContract_ContractId(contract.getContractId())
                ))
                .toList();
    }

    public ContractResponseDTO getContractDetail(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));
        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contractId)
        );
    }

    public ContractResponseDTO createContract(ContractCreateDTO request) {
        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người thuê: " + request.getTenantId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + request.getRoomId()));

        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setTenant(tenant);
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setActualRentPrice(request.getActualRentPrice());
        contract.setElecPriceOverride(request.getElecPriceOverride());
        contract.setWaterPriceOverride(request.getWaterPriceOverride());
        contract.setPenaltyTerms(request.getPenaltyTerms());
        contract.setStatus("ACTIVE");
        contract.setContractCode("HD-" + System.currentTimeMillis());

        if (request.getDepositId() != null) {
            depositRepository.findById(request.getDepositId()).ifPresent(deposit -> {
                contract.setDeposit(deposit);
                deposit.setStatus("COMPLETED");
                depositRepository.save(deposit);
            });
        }

        room.setStatus("RENTED");
        roomRepository.save(room);
        contractRepository.save(contract);

        return contractMapper.toDTO(contract,
                contractServiceRepository.findByContract_ContractId(contract.getContractId()));
    }

    public ContractResponseDTO extendContract(Long contractId, ContractExtendDTO request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));
        contract.setEndDate(request.getNewEndDate());
        contractRepository.save(contract);
        return contractMapper.toDTO(contract,
                contractServiceRepository.findByContract_ContractId(contractId));
    }

    public void terminateContract(Long contractId, User terminatedBy) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));
        contract.setStatus("TERMINATED_EARLY");
        contract.setTerminatedAt(LocalDateTime.now());
        contract.setTerminatedBy(terminatedBy);

        Room room = contract.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        contractRepository.save(contract);
    }

    public void addService(Long contractId, Long serviceId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));

        com.project.datalayer.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ: " + serviceId));

        ContractServiceId id = new ContractServiceId();
        id.setContractId(contractId);
        id.setServiceId(serviceId);

        ContractService cs = new ContractService();
        cs.setId(id);
        cs.setContract(contract);
        cs.setService(service);
        cs.setPriceSnapshot(service.getPrice());
        cs.setQuantity(1);

        contractServiceRepository.save(cs);
    }

    public void removeService(Long contractId, Long serviceId) {
        contractServiceRepository.deleteByContract_ContractIdAndService_ServiceId(contractId, serviceId);
    }
}