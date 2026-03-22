package com.project.datalayer.repository;

import com.project.datalayer.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByRoom_Area_Host_UserId(Long hostId);
    List<Deposit> findByTenant_UserId(Long tenantId);
}
