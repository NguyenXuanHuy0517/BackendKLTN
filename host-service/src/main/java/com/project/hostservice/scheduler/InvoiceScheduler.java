package com.project.hostservice.scheduler;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.Invoice;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Chạy lúc 01:00 ngày 1 hàng tháng.
     * Tạo hóa đơn DRAFT cho tất cả hợp đồng đang ACTIVE.
     *
     * FIX: Trước đây gọi findByRoom_Area_Host_UserId(null) → không trả về bản ghi nào.
     *      Nay dùng contractRepository.findAll() rồi filter status = ACTIVE.
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void createMonthlyInvoices() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        log.info("=== InvoiceScheduler: Bắt đầu tạo hóa đơn tháng {}/{} ===", month, year);

        // FIX: Lấy TẤT CẢ hợp đồng rồi filter ACTIVE thay vì truyền null
        List<Contract> activeContracts = contractRepository.findAll().stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .toList();

        log.info("Tìm thấy {} hợp đồng ACTIVE", activeContracts.size());

        int created = 0;
        int skipped = 0;

        for (Contract contract : activeContracts) {
            // Kiểm tra hóa đơn tháng này đã tồn tại chưa để tránh tạo trùng
            boolean exists = invoiceRepository.existsByContract_ContractIdAndBillingMonthAndBillingYear(
                    contract.getContractId(), month, year);

            if (exists) {
                skipped++;
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setContract(contract);

            // Mã hóa đơn theo format: INV-{contractId}-{year}{month}
            String invoiceCode = String.format("INV-%d-%d%02d",
                    contract.getContractId(), year, month);
            invoice.setInvoiceCode(invoiceCode);

            invoice.setBillingMonth(month);
            invoice.setBillingYear(year);

            // Hạn thanh toán: ngày 15 của tháng hiện tại
            invoice.setDueDate(LocalDate.of(year, month, 15));

            // Ghi sẵn tiền phòng từ hợp đồng
            invoice.setRentAmount(contract.getActualRentPrice());

            // Trạng thái DRAFT: chờ chủ trọ nhập chỉ số điện nước
            invoice.setStatus("DRAFT");

            invoiceRepository.save(invoice);
            created++;

            log.info("Tạo hóa đơn {} cho hợp đồng {} (tenant: {})",
                    invoiceCode,
                    contract.getContractCode(),
                    contract.getTenant().getFullName());
        }

        log.info("=== InvoiceScheduler hoàn thành: tạo mới {}, bỏ qua {} (đã tồn tại) ===",
                created, skipped);
    }
}