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
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    @Scheduled(cron = "0 0 1 1 * ?")
    public void createMonthlyInvoices() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        List<Contract> activeContracts = contractRepository.findByRoom_Area_Host_UserId(null).stream()
                .filter(c -> c.getStatus().equals("ACTIVE"))
                .toList();

        for (Contract contract : activeContracts) {
            boolean exists = invoiceRepository.existsByContract_ContractIdAndBillingMonthAndBillingYear(
                    contract.getContractId(), month, year);

            if (!exists) {
                Invoice invoice = new Invoice();
                invoice.setContract(contract);
                invoice.setInvoiceCode("INV-" + contract.getContractId() + "-" + year + month);
                invoice.setBillingMonth(month);
                invoice.setBillingYear(year);
                invoice.setDueDate(LocalDate.of(year, month, 15));
                invoice.setRentAmount(contract.getActualRentPrice());
                invoice.setStatus("UNPAID");
                invoiceRepository.save(invoice);
                log.info("Tạo hóa đơn: {} cho hợp đồng: {}", invoice.getInvoiceCode(), contract.getContractId());
            }
        }
    }
}