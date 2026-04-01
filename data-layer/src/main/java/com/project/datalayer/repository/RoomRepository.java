package com.project.datalayer.repository;

import com.project.datalayer.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByArea_AreaId(Long areaId);
    List<Room> findByArea_Host_UserId(Long hostId);
    List<Room> findByArea_AreaIdAndStatus(Long areaId, String status);
    int countByArea_AreaIdAndStatus(Long areaId, String status);

    // NEW - Count rooms without invoice for current month by host
    @Query("SELECT COUNT(DISTINCT r.roomId) FROM Room r " +
           "JOIN Contract c ON r.roomId = c.room.roomId " +
           "WHERE r.area.host.userId = :hostId " +
           "AND c.status = 'ACTIVE' " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM Invoice i " +
           "  WHERE i.contract.contractId = c.contractId " +
           "  AND i.billingMonth = :month " +
           "  AND i.billingYear = :year" +
           ")")
    Long countRoomsWithoutInvoiceByHostId(
            @Param("hostId") Long hostId,
            @Param("month") int month,
            @Param("year") int year
    );

    // NEW - Count total rooms rented (occupancy)
    @Query("SELECT COUNT(r) FROM Room r WHERE r.status IN ('DEPOSITED', 'RENTED')")
    Long countRentedRooms();

    // NEW - Count total rooms
    @Query("SELECT COUNT(r) FROM Room r")
    Long countTotalRooms();
}
