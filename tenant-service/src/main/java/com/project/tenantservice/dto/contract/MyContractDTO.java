package com.project.tenantservice.dto.contract;

import com.project.hostservice.dto.contract.ContractServiceDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến my contract để trao đổi giữa các tầng.
 */
@Data
public class MyContractDTO {
    private Long contractId;
    private String contractCode;
    private String roomCode;
    private String areaName;
    private String areaAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private String status;

    
    private List<ContractServiceDTO> contractServices;

    
    private List<String> serviceNames;
    private long daysUntilExpiry;
}
