package com.project.adminservice.dto.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHostStatusUpdateRequest {
    private boolean active;          // true = enable, false = disable
    private String reason;           // Required: reason for status change
    private String note;             // Optional: additional notes
}

