package com.project.datalayer.repository;

import com.project.datalayer.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByRoom_Area_Host_UserId(Long hostId);
    List<Issue> findByTenant_UserId(Long tenantId);
    List<Issue> findByStatus(String status);
}
