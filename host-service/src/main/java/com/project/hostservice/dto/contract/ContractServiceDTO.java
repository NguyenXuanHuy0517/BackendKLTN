package com.project.hostservice.dto.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractServiceDTO {
    private Long contractServiceId;    // PK trong bảng contract_services
    private Long serviceId;
    private String serviceName;
    private Integer quantity;
    private BigDecimal price;          // giá trong contract
    private String unitName;

    // Optional - thêm nếu cần tracking giá thay đổi
    private BigDecimal currentServicePrice;  // giá hiện tại của service
}

