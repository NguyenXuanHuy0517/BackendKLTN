package com.project.tenantservice.mapper;

import com.project.datalayer.entity.ContractService;
import com.project.datalayer.entity.Invoice;
import com.project.tenantservice.dto.invoice.MyInvoiceDTO;
import com.project.tenantservice.dto.invoice.MyInvoiceDetailDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {

    public MyInvoiceDTO toDTO(Invoice invoice) {
        MyInvoiceDTO dto = new MyInvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        return dto;
    }

    public MyInvoiceDetailDTO toDetailDTO(Invoice invoice,
                                          List<ContractService> services) {
        MyInvoiceDetailDTO dto = new MyInvoiceDetailDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());
        dto.setRoomCode(invoice.getContract().getRoom().getRoomCode());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setDueDate(invoice.getDueDate());
        dto.setElecOld(invoice.getElecOld());
        dto.setElecNew(invoice.getElecNew());
        dto.setElecPrice(invoice.getElecPrice());
        dto.setElecAmount(invoice.getElecAmount());
        dto.setWaterOld(invoice.getWaterOld());
        dto.setWaterNew(invoice.getWaterNew());
        dto.setWaterPrice(invoice.getWaterPrice());
        dto.setWaterAmount(invoice.getWaterAmount());
        dto.setRentAmount(invoice.getRentAmount());
        dto.setServiceAmount(invoice.getServiceAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setPaidAt(invoice.getPaidAt());
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());
        return dto;
    }
}