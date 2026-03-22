package com.project.tenantservice.service;

import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.exception.ResourceNotFoundException;
import com.project.tenantservice.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyContractService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ContractMapper contractMapper;

    public List<MyContractDTO> getMyContracts(Long tenantId) {
        return contractRepository.findByTenant_UserId(tenantId).stream()
                .map(contract -> contractMapper.toDTO(
                        contract,
                        contractServiceRepository
                                .findByContract_ContractId(contract.getContractId())))
                .toList();
    }

    public MyContractDTO getCurrentContract(Long tenantId) {
        return contractRepository.findByTenant_UserId(tenantId).stream()
                .filter(c -> c.getStatus().equals("ACTIVE"))
                .findFirst()
                .map(contract -> contractMapper.toDTO(
                        contract,
                        contractServiceRepository
                                .findByContract_ContractId(contract.getContractId())))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không có hợp đồng đang hiệu lực"));
    }
}