package com.project.tenantservice.service;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.ContractService;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.exception.ResourceNotFoundException;
import com.project.tenantservice.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyContractService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ContractMapper contractMapper;

    public List<MyContractDTO> getMyContracts(Long tenantId) {
        List<Contract> contracts = contractRepository.findWithRelationsByTenantId(tenantId);
        Map<Long, List<ContractService>> servicesByContractId = mapContractServicesByContractId(contracts);

        return contracts.stream()
                .map(contract -> contractMapper.toDTO(
                        contract,
                        servicesByContractId.getOrDefault(contract.getContractId(), Collections.emptyList())
                ))
                .toList();
    }

    public MyContractDTO getCurrentContract(Long tenantId) {
        return contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(tenantId, "ACTIVE")
                .map(contract -> contractMapper.toDTO(
                        contract,
                        contractServiceRepository.findByContract_ContractId(contract.getContractId())
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Khong co hop dong dang hieu luc"));
    }

    private Map<Long, List<ContractService>> mapContractServicesByContractId(List<Contract> contracts) {
        List<Long> contractIds = contracts.stream()
                .map(Contract::getContractId)
                .toList();

        if (contractIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return contractServiceRepository.findByContract_ContractIdIn(contractIds).stream()
                .collect(Collectors.groupingBy(contractService -> contractService.getContract().getContractId()));
    }
}
