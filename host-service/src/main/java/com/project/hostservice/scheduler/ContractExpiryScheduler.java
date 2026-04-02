package com.project.hostservice.scheduler;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.Room;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.RoomRepository;
import com.project.hostservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Vai trò: Scheduler của module host-service.
 * Chức năng: Thực thi các tác vụ nền liên quan đến contract expiry theo lịch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractExpiryScheduler {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;

    

        /**
     * Chức năng: Thực hiện nghiệp vụ expire contracts.
     */
@Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    public void expireContracts() {
        LocalDate today = LocalDate.now();
        log.info("=== ContractExpiryScheduler: Kiểm tra hợp đồng hết hạn ngày {} ===", today);

        
        List<Contract> expiredContracts = contractRepository
                .findByStatusAndEndDateBefore("ACTIVE", today);

        log.info("Tìm thấy {} hợp đồng cần chuyển EXPIRED", expiredContracts.size());

        int count = 0;
        for (Contract contract : expiredContracts) {
            
            contract.setStatus("EXPIRED");
            contractRepository.save(contract);

            
            Room room = contract.getRoom();
            room.setStatus("AVAILABLE");
            roomRepository.save(room);

            
            try {
                Long tenantId = contract.getTenant().getUserId();
                notificationService.sendToUser(
                        tenantId,
                        "CONTRACT_EXPIRED",
                        "Hợp đồng đã hết hạn",
                        "Hợp đồng " + contract.getContractCode() + " của bạn đã hết hạn. "
                                + "Vui lòng liên hệ chủ trọ để gia hạn hoặc trả phòng.",
                        "CONTRACT",
                        contract.getContractId()
                );
            } catch (Exception e) {
                log.warn("Không gửi được thông báo cho contract {}: {}",
                        contract.getContractCode(), e.getMessage());
            }

            count++;
            log.info("Hợp đồng {} → EXPIRED, phòng {} → AVAILABLE",
                    contract.getContractCode(),
                    room.getRoomCode());
        }

        log.info("=== ContractExpiryScheduler hoàn thành: xử lý {} hợp đồng ===", count);
    }
}
