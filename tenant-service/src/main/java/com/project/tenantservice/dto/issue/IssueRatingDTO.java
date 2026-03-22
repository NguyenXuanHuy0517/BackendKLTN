package com.project.tenantservice.dto.issue;

import lombok.Data;

@Data
public class IssueRatingDTO {
    private Integer rating;       // 1-5
    private String tenantFeedback;
}