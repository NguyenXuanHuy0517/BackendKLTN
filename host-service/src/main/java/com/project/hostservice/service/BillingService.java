package com.project.hostservice.service;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.ContractService;
import com.project.datalayer.entity.Invoice;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.InvoiceRepository;
import com.project.hostservice.dto.invoice.InvoiceDetailDTO;
import com.project.hostservice.dto.invoice.InvoiceResponseDTO;
import com.project.hostservice.dto.invoice.MeterReadingDTO;
import com.project.hostservice.exception.ResourceNotFoundException;
import com.project.hostservice.mapper.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceMapper invoiceMapper;

    public List<InvoiceResponseDTO> getInvoicesByHost(Long hostId) {
        return invoiceRepository.findByContract_Room_Area_Host_UserId(hostId).stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    public InvoiceDetailDTO getInvoiceDetail(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn: " + invoiceId));
        List<ContractService> services = contractServiceRepository
                .findByContract_ContractId(invoice.getContract().getContractId());
        return invoiceMapper.toDetailDTO(invoice, services);
    }

    public List<InvoiceResponseDTO> getOverdueInvoices(Long hostId) {
        return invoiceRepository.findByContract_Room_Area_Host_UserId(hostId).stream()
                .filter(i -> i.getStatus().equals("OVERDUE"))
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    public InvoiceDetailDTO updateMeterReading(Long invoiceId, MeterReadingDTO request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn: " + invoiceId));

        Contract contract = invoice.getContract();
        List<ContractService> services = contractServiceRepository
                .findByContract_ContractId(contract.getContractId());

        BigDecimal elecPrice = contract.getElecPriceOverride() != null
                ? contract.getElecPriceOverride()
                : contract.getRoom().getElecPrice();

        BigDecimal waterPrice = contract.getWaterPriceOverride() != null
                ? contract.getWaterPriceOverride()
                : contract.getRoom().getWaterPrice();

        BigDecimal elecAmount = elecPrice.multiply(
                BigDecimal.valueOf(request.getElecNew() - request.getElecOld()));
        BigDecimal waterAmount = waterPrice.multiply(
                BigDecimal.valueOf(request.getWaterNew() - request.getWaterOld()));
        BigDecimal serviceAmount = services.stream()
                .map(cs -> cs.getPriceSnapshot().multiply(BigDecimal.valueOf(cs.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setElecOld(request.getElecOld());
        invoice.setElecNew(request.getElecNew());
        invoice.setElecPrice(elecPrice);
        invoice.setWaterOld(request.getWaterOld());
        invoice.setWaterNew(request.getWaterNew());
        invoice.setWaterPrice(waterPrice);
        invoice.setElecAmount(elecAmount);
        invoice.setWaterAmount(waterAmount);
        invoice.setServiceAmount(serviceAmount);
        invoice.setRentAmount(contract.getActualRentPrice());
        invoice.setTotalAmount(contract.getActualRentPrice()
                .add(elecAmount).add(waterAmount).add(serviceAmount));
        invoice.setStatus("UNPAID");

        invoiceRepository.save(invoice);
        return invoiceMapper.toDetailDTO(invoice, services);
    }

    public void confirmPayment(Long invoiceId, User paidBy) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn: " + invoiceId));
        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setPaidBy(paidBy);
        invoiceRepository.save(invoice);
    }
}