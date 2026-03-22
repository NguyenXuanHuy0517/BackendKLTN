package com.project.hostservice.service;

import com.project.datalayer.entity.Issue;
import com.project.datalayer.repository.IssueRepository;
import com.project.hostservice.dto.issue.IssueResponseDTO;
import com.project.hostservice.dto.issue.IssueStatusUpdateDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueMapper issueMapper;

    public List<IssueResponseDTO> getIssuesByHost(Long hostId) {
        return issueRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .map(issueMapper::toDTO)
                .toList();
    }

    public IssueResponseDTO getIssueDetail(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khiếu nại: " + issueId));
        return issueMapper.toDTO(issue);
    }

    public void updateStatus(Long issueId, IssueStatusUpdateDTO request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khiếu nại: " + issueId));
        issue.setStatus(request.getStatus());
        issue.setHandlerNote(request.getHandlerNote());
        if (request.getStatus().equals("RESOLVED")) {
            issue.setResolvedAt(LocalDateTime.now());
        }
        issueRepository.save(issue);
    }
}