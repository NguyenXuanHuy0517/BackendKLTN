package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.chatbot.ChatRequestDTO;
import com.project.tenantservice.dto.chatbot.ChatResponseDTO;
import com.project.tenantservice.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ chatbot và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

        /**
     * Chức năng: Thực hiện nghiệp vụ chat.
     * URL: POST /api/tenant/chatbot
     */
@PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDTO>> chat(
            @RequestParam Long userId,
            @RequestBody ChatRequestDTO request) {
        log.info("POST /api/tenant/chatbot - userId: {}, message: {}",
                userId, request.getMessage());
        return ResponseEntity.ok(
                ApiResponse.success(chatbotService.chat(userId, request)));
    }
}
