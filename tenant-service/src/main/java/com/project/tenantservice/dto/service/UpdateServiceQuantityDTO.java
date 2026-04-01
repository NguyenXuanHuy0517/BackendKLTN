package com.project.tenantservice.dto.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceQuantityDTO {
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}



