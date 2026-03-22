package com.project.datalayer.repository;

import com.project.datalayer.entity.RoomStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomStatusHistoryRepository extends JpaRepository<RoomStatusHistory, Long> {
    List<RoomStatusHistory> findByRoom_RoomIdOrderByChangedAtDesc(Long roomId);
}