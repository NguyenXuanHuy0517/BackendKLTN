package com.project.hostservice.mapper;

import com.project.datalayer.entity.Issue;
import com.project.hostservice.dto.issue.IssueResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public IssueResponseDTO toDTO(Issue issue) {
        IssueResponseDTO dto = new IssueResponseDTO();
        dto.setIssueId(issue.getIssueId());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setTenantName(issue.getTenant().getFullName());
        dto.setRoomCode(issue.getRoom().getRoomCode());
        dto.setPriority(issue.getPriority());
        dto.setStatus(issue.getStatus());
        dto.setHandlerNote(issue.getHandlerNote());
        dto.setRating(issue.getRating());
        dto.setTenantFeedback(issue.getTenantFeedback());
        dto.setCreatedAt(issue.getCreatedAt());

        // NEW - Issue type and service suggestion fields
        dto.setIssueType(issue.getIssueType());
        if (issue.getArea() != null) {
            dto.setAreaName(issue.getArea().getAreaName());
            dto.setAreaId(issue.getArea().getAreaId());
        }
        dto.setSuggestedServiceName(issue.getSuggestedServiceName());
        dto.setSuggestionNote(issue.getSuggestionNote());
        return dto;
    }
}