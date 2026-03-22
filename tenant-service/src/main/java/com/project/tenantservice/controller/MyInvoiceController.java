package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.invoice.MyInvoiceDTO;
import com.project.tenantservice.dto.invoice.MyInvoiceDetailDTO;
import com.project.tenantservice.service.MyInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tenant/invoices")
@RequiredArgsConstructor
public class MyInvoiceController {

    private final MyInvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MyInvoiceDTO>>> getMyInvoices(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/invoices - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(invoiceService.getMyInvoices(userId)));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<MyInvoiceDetailDTO>> getInvoiceDetail(
            @PathVariable Long invoiceId,
            @RequestParam Long userId) {
        log.info("GET /api/tenant/invoices/{} - userId: {}", invoiceId, userId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        invoiceService.getInvoiceDetail(invoiceId, userId)));
    }
}