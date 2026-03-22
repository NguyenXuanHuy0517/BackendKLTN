package com.project.tenantservice.mapper;

import com.project.datalayer.entity.Issue;
import com.project.tenantservice.dto.issue.IssueResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public IssueResponseDTO toDTO(Issue issue) {
        IssueResponseDTO dto = new IssueResponseDTO();
        dto.setIssueId(issue.getIssueId());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setRoomCode(issue.getRoom().getRoomCode());
        dto.setPriority(issue.getPriority());
        dto.setStatus(issue.getStatus());
        dto.setHandlerNote(issue.getHandlerNote());
        dto.setRating(issue.getRating());
        dto.setTenantFeedback(issue.getTenantFeedback());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setResolvedAt(issue.getResolvedAt());
        return dto;
    }
}