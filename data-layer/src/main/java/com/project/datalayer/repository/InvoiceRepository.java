package com.project.datalayer.repository;

import com.project.datalayer.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByContract_Room_Area_Host_UserId(Long hostId);
    List<Invoice> findByContract_ContractId(Long contractId);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByStatusIn(List<String> statuses);
    boolean existsByContract_ContractIdAndBillingMonthAndBillingYear(Long contractId, int month, int year);
}