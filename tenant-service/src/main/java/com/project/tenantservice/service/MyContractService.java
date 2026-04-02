package com.project.tenantservice.service;

import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.exception.ResourceNotFoundException;
import com.project.tenantservice.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến my contract.
 */
@Service
@RequiredArgsConstructor
public class MyContractService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ContractMapper contractMapper;

        /**
     * Chức năng: Lấy dữ liệu my contracts.
     */
public List<MyContractDTO> getMyContracts(Long tenantId) {
        return contractRepository.findByTenant_UserId(tenantId).stream()
                .map(contract -> contractMapper.toDTO(
                        contract,
                        contractServiceRepository
                                .findByContract_ContractId(contract.getContractId())))
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu current contract.
     */
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
