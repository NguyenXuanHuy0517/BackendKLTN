package com.project.hostservice.dto.tenant;

import lombok.Data;

@Data
public class TenantCreateDTO {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String idCardNumber;
}