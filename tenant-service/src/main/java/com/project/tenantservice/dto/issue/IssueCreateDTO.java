package com.project.tenantservice.dto.issue;

import lombok.Data;

@Data
public class IssueCreateDTO {
    private String title;
    private String description;
    private String images;   // JSON string
    private String priority; // LOW | MEDIUM | HIGH | URGENT

    // NEW - Issue type
    private String issueType = "GENERAL";  // GENERAL, MAINTENANCE, SERVICE_SUGGESTION

    // NEW - Service suggestion fields (required nếu issueType = SERVICE_SUGGESTION)
    private String suggestedServiceName;
    private String suggestionNote;
}