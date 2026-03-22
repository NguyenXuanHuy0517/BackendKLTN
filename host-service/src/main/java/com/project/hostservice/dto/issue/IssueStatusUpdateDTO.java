package com.project.hostservice.dto.issue;

import lombok.Data;

@Data
public class IssueStatusUpdateDTO {
    private String status;
    private String handlerNote;
}