package com.project.adminservice.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAlertDTO {
    private String type;        // "OVERDUE_INVOICE", "MISSING_INVOICE", "HOST_BLOCKED"
    private String message;
    private Integer count;      // Số lượng (nếu applicable)
    private String severity;    // "INFO", "WARNING", "ERROR"
}

