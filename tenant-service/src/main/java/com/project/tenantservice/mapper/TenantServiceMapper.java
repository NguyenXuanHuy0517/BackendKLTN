package com.project.tenantservice.mapper;

import com.project.datalayer.entity.Service;
import com.project.datalayer.entity.ContractService;
import com.project.tenantservice.dto.service.TenantServiceDTO;
import org.springframework.stereotype.Component;

@Component
public class TenantServiceMapper {

    public TenantServiceDTO toDTO(Service service) {
        if (service == null) {
            return null;
        }

        return new TenantServiceDTO(
                service.getServiceId(),
                service.getServiceName(),
                0,  // quantity = 0 (chưa có trong contract)
                service.getPrice(),
                service.getUnitName(),
                service.getDescription(),
                null,  // contractServiceId
                null,  // priceSnapshot
                null   // unitSnapshot
        );
    }

    // NEW - Map from ContractService (includes quantity and snapshot)
    public TenantServiceDTO toDTOFromContractService(ContractService contractService) {
        if (contractService == null || contractService.getService() == null) {
            return null;
        }

        Service service = contractService.getService();
        return new TenantServiceDTO(
                service.getServiceId(),
                service.getServiceName(),
                contractService.getQuantity(),
                service.getPrice(),
                service.getUnitName(),
                service.getDescription(),
                contractService.getId().getServiceId(),  // contractServiceId
                contractService.getPriceSnapshot(),      // priceSnapshot
                service.getUnitName()                    // unitSnapshot
        );
    }
}

