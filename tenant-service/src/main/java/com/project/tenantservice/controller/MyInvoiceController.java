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

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ my invoice và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/invoices")
@RequiredArgsConstructor
public class MyInvoiceController {

    private final MyInvoiceService invoiceService;

        /**
     * Chức năng: Lấy dữ liệu my invoices.
     * URL: GET /api/tenant/invoices
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<MyInvoiceDTO>>> getMyInvoices(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/invoices - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(invoiceService.getMyInvoices(userId)));
    }

        /**
     * Chức năng: Lấy dữ liệu invoice detail.
     * URL: GET /api/tenant/invoices/{invoiceId}
     */
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
