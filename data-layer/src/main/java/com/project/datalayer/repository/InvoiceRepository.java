package com.project.datalayer.repository;

import com.project.datalayer.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByContract_Room_Area_Host_UserId(Long hostId);
    List<Invoice> findByContract_ContractId(Long contractId);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByStatusIn(List<String> statuses);
    boolean existsByContract_ContractIdAndBillingMonthAndBillingYear(Long contractId, int month, int year);

    // NEW - Count overdue invoices by host
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.contract.room.area.host.userId = :hostId " +
           "AND i.status != 'PAID' AND i.dueDate < CURRENT_DATE")
    Long countOverdueByHostId(@Param("hostId") Long hostId);

    // NEW - Count overdue invoices globally
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status != 'PAID' AND i.dueDate < CURRENT_DATE")
    Long countOverdueInvoices();

    // NEW - Sum revenue all time
    @Query("SELECT COALESCE(SUM(i.rentAmount + i.elecAmount + i.waterAmount), 0) FROM Invoice i " +
           "WHERE i.status = 'PAID'")
    java.math.BigDecimal sumRevenueAllTime();

    // NEW - Sum paid invoices for period
    @Query("SELECT COALESCE(SUM(i.rentAmount + i.elecAmount + i.waterAmount), 0) FROM Invoice i " +
           "WHERE i.status = 'PAID' AND i.billingMonth = :month AND i.billingYear = :year")
    java.math.BigDecimal sumRevenueByPeriod(@Param("month") int month, @Param("year") int year);
}


