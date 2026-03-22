package com.project.datalayer.repository;

import com.project.datalayer.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByArea_AreaId(Long areaId);
    List<Room> findByArea_Host_UserId(Long hostId);
    List<Room> findByArea_AreaIdAndStatus(Long areaId, String status);
    int countByArea_AreaIdAndStatus(Long areaId, String status);
}
