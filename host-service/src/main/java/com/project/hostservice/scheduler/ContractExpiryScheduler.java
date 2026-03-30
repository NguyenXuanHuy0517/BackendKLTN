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

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler tự động hóa hợp đồng hết hạn.
 *
 * Luận văn (mục 3.2.5) đề cập: "Hàng ngày hệ thống kiểm tra và tự chuyển
 * trạng thái hợp đồng quá ngày kết thúc từ ACTIVE sang EXPIRED, đồng thời
 * cập nhật trạng thái phòng về AVAILABLE."
 *
 * File này bị thiếu trong code gốc — đây là bản bổ sung.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractExpiryScheduler {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;

    /**
     * Chạy lúc 00:30 hàng ngày.
     * Chuyển hợp đồng ACTIVE đã quá hạn → EXPIRED, phòng → AVAILABLE.
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void expireContracts() {
        LocalDate today = LocalDate.now();
        log.info("=== ContractExpiryScheduler: Kiểm tra hợp đồng hết hạn ngày {} ===", today);

        // Lấy tất cả hợp đồng ACTIVE có endDate < hôm nay
        List<Contract> expiredContracts = contractRepository
                .findByStatusAndEndDateBefore("ACTIVE", today);

        log.info("Tìm thấy {} hợp đồng cần chuyển EXPIRED", expiredContracts.size());

        int count = 0;
        for (Contract contract : expiredContracts) {
            // Chuyển hợp đồng sang EXPIRED
            contract.setStatus("EXPIRED");
            contractRepository.save(contract);

            // Cập nhật phòng về AVAILABLE
            Room room = contract.getRoom();
            room.setStatus("AVAILABLE");
            roomRepository.save(room);

            // Gửi thông báo cho người thuê
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