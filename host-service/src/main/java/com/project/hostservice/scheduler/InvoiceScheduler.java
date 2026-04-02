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

/**
 * Vai trò: Scheduler của module host-service.
 * Chức năng: Thực thi các tác vụ nền liên quan đến invoice theo lịch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    

        /**
     * Chức năng: Tạo monthly invoices.
     */
@Scheduled(cron = "0 0 1 1 * ?")
    public void createMonthlyInvoices() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year  = now.getYear();

        log.info("=== InvoiceScheduler: Bắt đầu tạo hóa đơn tháng {}/{} ===", month, year);

        
        List<Contract> activeContracts = contractRepository.findByStatus("ACTIVE");

        log.info("Tìm thấy {} hợp đồng ACTIVE", activeContracts.size());

        int created = 0;
        int skipped = 0;

        for (Contract contract : activeContracts) {
            boolean exists = invoiceRepository
                    .existsByContract_ContractIdAndBillingMonthAndBillingYear(
                            contract.getContractId(), month, year);

            if (exists) {
                skipped++;
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setContract(contract);

            String invoiceCode = String.format("INV-%d-%d%02d",
                    contract.getContractId(), year, month);
            invoice.setInvoiceCode(invoiceCode);
            invoice.setBillingMonth(month);
            invoice.setBillingYear(year);
            invoice.setDueDate(LocalDate.of(year, month, 15));
            invoice.setRentAmount(contract.getActualRentPrice());
            invoice.setStatus("UNPAID");   
            
            
            

            invoiceRepository.save(invoice);
            created++;

            log.info("Tạo hóa đơn {} cho hợp đồng {} (tenant: {})",
                    invoiceCode,
                    contract.getContractCode(),
                    contract.getTenant().getFullName());
        }

        log.info("=== InvoiceScheduler hoàn thành: tạo mới {}, bỏ qua {} ===",
                created, skipped);
    }
}
