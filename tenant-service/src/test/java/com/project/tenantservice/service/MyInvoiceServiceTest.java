package com.project.tenantservice.service;

import com.project.datalayer.entity.Invoice;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.InvoiceRepository;
import com.project.tenantservice.dto.invoice.MyInvoiceDTO;
import com.project.tenantservice.mapper.InvoiceMapper;
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

class MyInvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ContractServiceRepository contractServiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private MyInvoiceService myInvoiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMyInvoicesPageUsesTenantQueryAndMapsPage() {
        Invoice first = new Invoice();
        first.setInvoiceId(10L);
        Invoice second = new Invoice();
        second.setInvoiceId(20L);
        Page<Invoice> invoicePage = new PageImpl<>(List.of(first, second), PageRequest.of(0, 20), 2);

        MyInvoiceDTO firstDto = new MyInvoiceDTO();
        firstDto.setInvoiceId(10L);
        MyInvoiceDTO secondDto = new MyInvoiceDTO();
        secondDto.setInvoiceId(20L);

        when(invoiceRepository.findPageWithRelationsByTenantId(
                eq(8L),
                eq("PAID"),
                eq("room b"),
                any(Pageable.class)
        )).thenReturn(invoicePage);
        when(invoiceMapper.toDTO(first)).thenReturn(firstDto);
        when(invoiceMapper.toDTO(second)).thenReturn(secondDto);

        var response = myInvoiceService.getMyInvoicesPage(8L, " PAID ", " room b ", 0, 20, "createdAt,desc");

        assertEquals(2, response.getItems().size());
        assertSame(firstDto, response.getItems().get(0));
        assertSame(secondDto, response.getItems().get(1));
        assertEquals(2L, response.getTotalItems());
        assertEquals(0, response.getPage());
        assertEquals(20, response.getSize());
    }
}
