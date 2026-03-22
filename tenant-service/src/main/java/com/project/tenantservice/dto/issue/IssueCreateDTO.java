package com.project.tenantservice.dto.issue;

import lombok.Data;

@Data
public class IssueCreateDTO {
    private String title;
    private String description;
    private String images;   // JSON string
    private String priority; // LOW | MEDIUM | HIGH | URGENT
}