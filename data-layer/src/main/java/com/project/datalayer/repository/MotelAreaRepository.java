package com.project.datalayer.repository;

import com.project.datalayer.entity.MotelArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotelAreaRepository extends JpaRepository<MotelArea, Long> {
    List<MotelArea> findByHost_UserId(Long hostId);
}