package com.project.tenantservice.service;

import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.InvoiceRepository;
import com.project.tenantservice.dto.invoice.MyInvoiceDTO;
import com.project.tenantservice.dto.invoice.MyInvoiceDetailDTO;
import com.project.tenantservice.exception.ResourceNotFoundException;
import com.project.tenantservice.mapper.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến my invoice.
 */
@Service
@RequiredArgsConstructor
public class MyInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceMapper invoiceMapper;

        /**
     * Chức năng: Lấy dữ liệu my invoices.
     */
public List<MyInvoiceDTO> getMyInvoices(Long tenantId) {
        return invoiceRepository
                .findByContract_Room_Area_Host_UserId(tenantId).stream()
                .filter(i -> i.getContract().getTenant()
                        .getUserId().equals(tenantId))
                .map(invoiceMapper::toDTO)
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu invoice detail.
     */
public MyInvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long tenantId) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hóa đơn: " + invoiceId));

        
        if (!invoice.getContract().getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException("Không tìm thấy hóa đơn: " + invoiceId);
        }

        var services = contractServiceRepository
                .findByContract_ContractId(invoice.getContract().getContractId());

        return invoiceMapper.toDetailDTO(invoice, services);
    }
}
