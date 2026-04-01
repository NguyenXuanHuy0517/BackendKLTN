package com.project.hostservice.dto.issue;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IssueResponseDTO {
    private Long issueId;
    private String title;
    private String description;
    private String tenantName;
    private String roomCode;
    private String priority;
    private String status;
    private String handlerNote;
    private Integer rating;
    private String tenantFeedback;
    private LocalDateTime createdAt;

    // NEW - Issue type and service suggestion fields
    private String issueType;
    private String areaName;
    private Long areaId;
    private String suggestedServiceName;
    private String suggestionNote;
}