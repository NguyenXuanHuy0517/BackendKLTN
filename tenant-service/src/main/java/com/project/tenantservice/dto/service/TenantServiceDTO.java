package com.project.tenantservice.dto.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantServiceDTO {
    private Long serviceId;
    private String serviceName;
    private Integer quantity;           // NEW - số lượng tenant đang dùng
    private BigDecimal price;
    private String unitName;
    private String description;

    // NEW - Optional fields để track service changes
    private Long contractServiceId;     // NEW
    private BigDecimal priceSnapshot;   // NEW - giá khi ký hợp đồng
    private String unitSnapshot;        // NEW - đơn vị khi ký hợp đồng
}

