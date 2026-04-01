package com.project.tenantservice.dto.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddServiceToContractDTO {
    @NotNull(message = "Hợp đồng không được để trống")
    private Long contractId;

    @NotNull(message = "Dịch vụ không được để trống")
    private Long serviceId;

    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity = 1;
}



