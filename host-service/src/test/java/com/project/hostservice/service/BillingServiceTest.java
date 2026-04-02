package com.project.hostservice.service;

import com.project.datalayer.entity.Invoice;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.InvoiceRepository;
import com.project.hostservice.dto.invoice.InvoiceResponseDTO;
import com.project.hostservice.mapper.InvoiceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractServiceRepository contractServiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInvoicesPageUsesNormalizedFiltersAndMapsPage() {
        Invoice first = new Invoice();
        first.setInvoiceId(100L);
        Invoice second = new Invoice();
        second.setInvoiceId(200L);
        Page<Invoice> invoicePage = new PageImpl<>(List.of(first, second), PageRequest.of(0, 20), 2);

        InvoiceResponseDTO firstDto = new InvoiceResponseDTO();
        firstDto.setInvoiceId(1L);
        InvoiceResponseDTO secondDto = new InvoiceResponseDTO();
        secondDto.setInvoiceId(2L);

        when(invoiceRepository.findPageWithRelationsByHostId(
                eq(15L),
                eq("UNPAID"),
                eq("room a"),
                eq(3),
                eq(2026),
                any(Pageable.class)
        )).thenReturn(invoicePage);
        when(invoiceMapper.toResponseDTO(first)).thenReturn(firstDto);
        when(invoiceMapper.toResponseDTO(second)).thenReturn(secondDto);

        var response = billingService.getInvoicesPage(15L, " UNPAID ", " room a ", 3, 2026, 0, 20, "createdAt,desc");

        assertEquals(2, response.getItems().size());
        assertSame(firstDto, response.getItems().get(0));
        assertSame(secondDto, response.getItems().get(1));
        assertEquals(2L, response.getTotalItems());
        assertEquals(0, response.getPage());
        assertEquals(20, response.getSize());
    }
}
