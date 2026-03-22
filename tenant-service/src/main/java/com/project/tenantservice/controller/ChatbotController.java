package com.project.tenantservice.controller;

import com.project.datalayer.dto.common.ApiResponse;
import com.project.tenantservice.dto.chatbot.ChatRequestDTO;
import com.project.tenantservice.dto.chatbot.ChatResponseDTO;
import com.project.tenantservice.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tenant/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

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