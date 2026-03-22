package com.project.datalayer.repository;

import com.project.datalayer.entity.RoomAsset;
import com.project.datalayer.entity.RoomAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomAssetRepository extends JpaRepository<RoomAsset, RoomAssetId> {
    List<RoomAsset> findByRoom_RoomId(Long roomId);
    void deleteByRoom_RoomIdAndEquipment_EquipmentId(Long roomId, Long equipmentId);
}