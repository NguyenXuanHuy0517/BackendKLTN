package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.issue.IssueCreateDTO;
import com.project.tenantservice.dto.issue.IssueRatingDTO;
import com.project.tenantservice.dto.issue.IssueResponseDTO;
import com.project.tenantservice.service.MyIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tenant/issues")
@RequiredArgsConstructor
public class MyIssueController {

    private final MyIssueService issueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponseDTO>>> getMyIssues(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/issues - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(issueService.getMyIssues(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IssueResponseDTO>> createIssue(
            @RequestParam Long userId,
            @RequestBody IssueCreateDTO request) {
        log.info("POST /api/tenant/issues - userId: {}, title: {}",
                userId, request.getTitle());
        return ResponseEntity.ok(
                ApiResponse.success(issueService.createIssue(userId, request)));
    }

    @PatchMapping("/{issueId}/rating")
    public ResponseEntity<ApiResponse<IssueResponseDTO>> rateIssue(
            @PathVariable Long issueId,
            @RequestParam Long userId,
            @RequestBody IssueRatingDTO request) {
        log.info("PATCH /api/tenant/issues/{}/rating - userId: {}",
                issueId, userId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        issueService.rateIssue(issueId, userId, request)));
    }
}