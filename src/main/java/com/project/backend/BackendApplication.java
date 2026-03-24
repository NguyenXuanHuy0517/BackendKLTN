package com.project.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SmartRoom Motel Management Platform - Backend Application
 * 
 * Orchestrates all microservices:
 * - Auth Service (port 8081) - Authentication & JWT Token Management
 * - Host Service (port 8082) - Host/Landlord Business Logic
 * - Tenant Service (port 8083) - Tenant/Renter API
 * - Admin Service (port 8084) - Platform Administration
 * - Data Layer - Shared Entity Models & Repositories
 * 
 * Usage:
 *   mvn spring-boot:run -pl Backend
 */
@SpringBootApplication(scanBasePackages = {
    "com.project.backend",
    "com.project.authservice",
    "com.project.hostservice", 
    "com.project.tenantservice",
    "com.project.adminservice",
    "com.project.datalayer"
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
