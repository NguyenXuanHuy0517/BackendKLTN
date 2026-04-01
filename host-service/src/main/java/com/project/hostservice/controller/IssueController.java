package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.hostservice.dto.issue.IssueResponseDTO;
import com.project.hostservice.dto.issue.IssueStatusUpdateDTO;
import com.project.hostservice.service.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponseDTO>>> getIssues(@RequestParam Long hostId,
                                                                          @RequestParam(required = false) String issueType) {
        log.info("GET /api/host/issues - hostId: {}, issueType: {}", hostId, issueType);
        List<IssueResponseDTO> result;
        if (issueType != null && !issueType.isEmpty()) {
            result = issueService.getIssuesByHostAndType(hostId, issueType);
            log.info("GET /api/host/issues - trả về {} khiếu nại loại {}", result.size(), issueType);
        } else {
            result = issueService.getIssuesByHost(hostId);
            log.info("GET /api/host/issues - trả về {} khiếu nại", result.size());
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<ApiResponse<IssueResponseDTO>> getIssueDetail(@PathVariable Long issueId) {
        log.info("GET /api/host/issues/{}", issueId);
        IssueResponseDTO result = issueService.getIssueDetail(issueId);
        log.info("GET /api/host/issues/{} - title: {}", issueId, result.getTitle());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{issueId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long issueId,
                                                          @RequestBody IssueStatusUpdateDTO request) {
        log.info("PATCH /api/host/issues/{}/status - status: {}", issueId, request.getStatus());
        issueService.updateStatus(issueId, request);
        log.info("PATCH /api/host/issues/{}/status - cập nhật thành công", issueId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}