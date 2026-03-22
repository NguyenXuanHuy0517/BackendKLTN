package com.project.tenantservice.dto.chatbot;

import lombok.Data;

@Data
public class ChatResponseDTO {
    private String reply;
    private String intentDetected;
}