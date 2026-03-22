package com.project.datalayer.repository;

import com.project.datalayer.entity.ChatbotHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, Long> {
    List<ChatbotHistory> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
