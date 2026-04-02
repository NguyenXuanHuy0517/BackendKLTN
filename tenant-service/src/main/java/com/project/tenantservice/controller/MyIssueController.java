package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.datalayer.dto.common.PagedResponse;
import com.project.tenantservice.dto.issue.IssueCreateDTO;
import com.project.tenantservice.dto.issue.IssueRatingDTO;
import com.project.tenantservice.dto.issue.IssueResponseDTO;
import com.project.tenantservice.service.MyIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/issues")
@RequiredArgsConstructor
public class MyIssueController {

    private final MyIssueService issueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponseDTO>>> getMyIssues(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(issueService.getMyIssues(userId)));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<IssueResponseDTO>>> getMyIssuesPaged(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                issueService.getMyIssuesPage(userId, status, search, page, size, sort)
        ));
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<ApiResponse<IssueResponseDTO>> getIssueDetail(
            @PathVariable Long issueId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(issueService.getIssueDetail(issueId, userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IssueResponseDTO>> createIssue(
            @RequestParam Long userId,
            @RequestBody IssueCreateDTO request) {
        return ResponseEntity.ok(ApiResponse.success(issueService.createIssue(userId, request)));
    }

    @PatchMapping("/{issueId}/rating")
    public ResponseEntity<ApiResponse<IssueResponseDTO>> rateIssue(
            @PathVariable Long issueId,
            @RequestParam Long userId,
            @RequestBody IssueRatingDTO request) {
        return ResponseEntity.ok(ApiResponse.success(issueService.rateIssue(issueId, userId, request)));
    }
}
