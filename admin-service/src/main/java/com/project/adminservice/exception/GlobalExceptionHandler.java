package com.project.adminservice.exception;

import com.project.datalayer.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Vai trò: REST controller của module admin-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ global exception handler và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
     * Chức năng: Xử lý resource not found.
     * URL: REQUEST /
     */
@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException e) {
        log.error("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

        /**
     * Chức năng: Xử lý general exception.
     * URL: REQUEST /
     */
@ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception e) {
        log.error("General error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
    }
}
