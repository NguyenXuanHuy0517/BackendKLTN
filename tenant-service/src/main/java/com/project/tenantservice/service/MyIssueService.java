package com.project.tenantservice.service;

import com.project.datalayer.entity.Issue;
import com.project.datalayer.entity.Room;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.IssueRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.tenantservice.dto.issue.IssueCreateDTO;
import com.project.tenantservice.dto.issue.IssueRatingDTO;
import com.project.tenantservice.dto.issue.IssueResponseDTO;
import com.project.tenantservice.exception.ResourceNotFoundException;
import com.project.tenantservice.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyIssueService {

    private final IssueRepository issueRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final IssueMapper issueMapper;

    public List<IssueResponseDTO> getMyIssues(Long tenantId) {
        return issueRepository.findByTenant_UserId(tenantId).stream()
                .map(issueMapper::toDTO)
                .toList();
    }

    public IssueResponseDTO createIssue(Long tenantId, IssueCreateDTO request) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + tenantId));

        // Lấy phòng từ hợp đồng ACTIVE hiện tại
        Room room = contractRepository.findByTenant_UserId(tenantId).stream()
                .filter(c -> c.getStatus().equals("ACTIVE"))
                .findFirst()
                .map(c -> c.getRoom())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bạn không có hợp đồng đang hiệu lực"));

        Issue issue = new Issue();
        issue.setTenant(tenant);
        issue.setRoom(room);
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setImages(request.getImages());
        issue.setPriority(request.getPriority() != null
                ? request.getPriority() : "MEDIUM");
        issue.setStatus("OPEN");

        // NEW - Set issue type and service suggestion fields
        issue.setIssueType(request.getIssueType() != null
                ? request.getIssueType() : "GENERAL");
        if ("SERVICE_SUGGESTION".equals(request.getIssueType())) {
            issue.setSuggestedServiceName(request.getSuggestedServiceName());
            issue.setSuggestionNote(request.getSuggestionNote());
            issue.setArea(room.getArea());
        }

        issueRepository.save(issue);
        return issueMapper.toDTO(issue);
    }

    public IssueResponseDTO rateIssue(Long issueId, Long tenantId,
                                      IssueRatingDTO request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy khiếu nại: " + issueId));

        if (!issue.getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy khiếu nại: " + issueId);
        }

        if (!issue.getStatus().equals("RESOLVED")) {
            throw new IllegalStateException(
                    "Chỉ có thể đánh giá khiếu nại đã được giải quyết");
        }

        issue.setRating(request.getRating());
        issue.setTenantFeedback(request.getTenantFeedback());
        issue.setStatus("CLOSED");
        issue.setClosedAt(LocalDateTime.now());

        issueRepository.save(issue);
        return issueMapper.toDTO(issue);
    }
}