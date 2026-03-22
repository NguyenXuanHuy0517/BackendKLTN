package com.project.tenantservice.dto.issue;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IssueResponseDTO {
    private Long issueId;
    private String title;
    private String description;
    private String roomCode;
    private String priority;
    private String status;
    private String handlerNote;
    private Integer rating;
    private String tenantFeedback;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}