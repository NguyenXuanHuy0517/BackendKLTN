package com.project.datalayer.repository;

import com.project.datalayer.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByRoom_Area_Host_UserId(Long hostId);
    List<Contract> findByTenant_UserId(Long tenantId);
    Optional<Contract> findByRoom_RoomIdAndStatus(Long roomId, String status);
    List<Contract> findByStatusAndEndDateBefore(String status, LocalDate date);
}
