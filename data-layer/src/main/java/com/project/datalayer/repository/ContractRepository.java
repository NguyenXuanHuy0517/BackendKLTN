package com.project.datalayer.repository;

import com.project.datalayer.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * FIX: Thêm method này — được dùng bởi InvoiceScheduler và ContractExpiryScheduler.
     * Trước đây thiếu method này nên InvoiceScheduler phải dùng findAll().stream().filter()
     * hoặc truyền null vào findByRoom_Area_Host_UserId().
     */
    List<Contract> findByStatus(String status);

    // NEW - Count active contracts by host
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.room.area.host.userId = :hostId AND c.status = :status")
    Long countByRoom_Area_Host_UserIdAndStatus(@Param("hostId") Long hostId, @Param("status") String status);

    // NEW - Count total active contracts
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.status = 'ACTIVE'")
    Long countActiveContracts();
}