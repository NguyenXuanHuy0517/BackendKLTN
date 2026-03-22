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

@Service
@RequiredArgsConstructor
public class MyInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceMapper invoiceMapper;

    public List<MyInvoiceDTO> getMyInvoices(Long tenantId) {
        return invoiceRepository
                .findByContract_Room_Area_Host_UserId(tenantId).stream()
                .filter(i -> i.getContract().getTenant()
                        .getUserId().equals(tenantId))
                .map(invoiceMapper::toDTO)
                .toList();
    }

    public MyInvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long tenantId) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy hóa đơn: " + invoiceId));

        // Kiểm tra hóa đơn có thuộc về tenant này không
        if (!invoice.getContract().getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException("Không tìm thấy hóa đơn: " + invoiceId);
        }

        var services = contractServiceRepository
                .findByContract_ContractId(invoice.getContract().getContractId());

        return invoiceMapper.toDetailDTO(invoice, services);
    }
}