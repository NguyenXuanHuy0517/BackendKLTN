package com.project.datalayer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    @Column(name = "priority", nullable = false, length = 10)
    private String priority = "MEDIUM";     // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "status", nullable = false, length = 15)
    private String status = "OPEN";         // OPEN, PROCESSING, RESOLVED, CLOSED

    @Column(name = "handler_note", columnDefinition = "TEXT")
    private String handlerNote;

    @Column(name = "rating")
    private Integer rating;                 // 1-5 sao

    @Column(name = "tenant_feedback", columnDefinition = "TEXT")
    private String tenantFeedback;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}