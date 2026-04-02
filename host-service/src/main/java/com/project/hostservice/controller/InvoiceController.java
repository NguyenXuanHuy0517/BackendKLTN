package com.project.hostservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.datalayer.entity.User;
import com.project.hostservice.dto.invoice.InvoiceDetailDTO;
import com.project.hostservice.dto.invoice.InvoiceResponseDTO;
import com.project.hostservice.dto.invoice.MeterReadingDTO;
import com.project.hostservice.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ invoice và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final BillingService billingService;

        /**
     * Chức năng: Lấy dữ liệu invoices.
     * URL: GET /api/host/invoices
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponseDTO>>> getInvoices(@RequestParam Long hostId) {
        log.info("GET /api/host/invoices - hostId: {}", hostId);
        List<InvoiceResponseDTO> result = billingService.getInvoicesByHost(hostId);
        log.info("GET /api/host/invoices - trả về {} hóa đơn", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu invoice detail.
     * URL: GET /api/host/invoices/{invoiceId}
     */
@GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceDetailDTO>> getInvoiceDetail(@PathVariable Long invoiceId) {
        log.info("GET /api/host/invoices/{}", invoiceId);
        InvoiceDetailDTO result = billingService.getInvoiceDetail(invoiceId);
        log.info("GET /api/host/invoices/{} - invoiceCode: {}", invoiceId, result.getInvoiceCode());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu overdue invoices.
     * URL: GET /api/host/invoices/overdue
     */
@GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<InvoiceResponseDTO>>> getOverdueInvoices(@RequestParam Long hostId) {
        log.info("GET /api/host/invoices/overdue - hostId: {}", hostId);
        List<InvoiceResponseDTO> result = billingService.getOverdueInvoices(hostId);
        log.info("GET /api/host/invoices/overdue - trả về {} hóa đơn quá hạn", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Cập nhật meters.
     * URL: PUT /api/host/invoices/{invoiceId}/meters
     */
@PutMapping("/{invoiceId}/meters")
    public ResponseEntity<ApiResponse<InvoiceDetailDTO>> updateMeters(@PathVariable Long invoiceId,
                                                                      @RequestBody MeterReadingDTO request) {
        log.info("PUT /api/host/invoices/{}/meters - elecNew: {}, waterNew: {}",
                invoiceId, request.getElecNew(), request.getWaterNew());
        InvoiceDetailDTO result = billingService.updateMeterReading(invoiceId, request);
        log.info("PUT /api/host/invoices/{}/meters - tổng tiền: {}", invoiceId, result.getTotalAmount());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ confirm payment.
     * URL: PATCH /api/host/invoices/{invoiceId}/pay
     */
@PatchMapping("/{invoiceId}/pay")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@PathVariable Long invoiceId,
                                                            @RequestParam Long paidById) {
        log.info("PATCH /api/host/invoices/{}/pay - paidById: {}", invoiceId, paidById);
        User paidBy = new User();
        paidBy.setUserId(paidById);
        billingService.confirmPayment(invoiceId, paidBy);
        log.info("PATCH /api/host/invoices/{}/pay - xác nhận thanh toán thành công", invoiceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
