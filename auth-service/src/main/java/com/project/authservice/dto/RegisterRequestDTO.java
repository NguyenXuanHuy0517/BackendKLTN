package com.project.authservice.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String idCardNumber;
}