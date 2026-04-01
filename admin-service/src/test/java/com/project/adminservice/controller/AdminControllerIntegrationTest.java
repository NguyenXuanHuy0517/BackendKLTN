package com.project.adminservice.controller;

import com.project.adminservice.dto.dashboard.AdminDashboardDTO;
import com.project.adminservice.dto.host.AdminHostDetailDTO;
import com.project.adminservice.dto.host.AdminHostStatusUpdateRequest;
import com.project.adminservice.service.AdminDashboardService;
import com.project.adminservice.service.AdminHostService;
import com.project.adminservice.service.AdminRevenueService;
import com.project.adminservice.service.AdminRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private AdminDashboardService dashboardService;

    @MockitoBean
    private AdminHostService hostService;

    @MockitoBean
    private AdminRoomService roomService;

    @MockitoBean
    private AdminRevenueService revenueService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    // ============ Security Tests ============

    @Test
    public void testDashboardWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "HOST")
    public void testDashboardWithHostRole() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    public void testDashboardWithTenantRole() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDashboardWithAdminRole() throws Exception {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        dashboard.setTotalUsers(100L);
        dashboard.setTotalHosts(10L);
        dashboard.setTotalTenants(50L);
        dashboard.setTotalRooms(100L);
        dashboard.setTotalContracts(50L);
        dashboard.setOccupancyRate(85L);
        dashboard.setTotalRevenue(new BigDecimal("1000000.00"));
        dashboard.setThisMonthRevenue(new BigDecimal("100000.00"));
        dashboard.setOverdueInvoices(5L);
        dashboard.setActiveContracts(45L);
        dashboard.setAlerts(new ArrayList<>());

        org.mockito.Mockito.when(dashboardService.getDashboard()).thenReturn(dashboard);

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(100));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testHostsWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/hosts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testHostDetailWithAdminRole() throws Exception {
        AdminHostDetailDTO host = new AdminHostDetailDTO();
        host.setUserId(1L);
        host.setFullName("Test Host");
        host.setEmail("host@test.com");
        host.setActive(true);
        host.setTotalAreas(5L);
        host.setTotalRooms(50L);
        host.setActiveContracts(40L);
        host.setOverdueInvoices(2L);
        host.setRoomsWithoutInvoice(3L);

        org.mockito.Mockito.when(hostService.getHostDetail(1L)).thenReturn(host);

        mockMvc.perform(get("/api/admin/hosts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Test Host"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateHostStatus() throws Exception {
        AdminHostStatusUpdateRequest request = new AdminHostStatusUpdateRequest();
        request.setActive(false);
        request.setReason("Vi phạm quy định");
        request.setNote("Khóa tạm thời");

        mockMvc.perform(patch("/api/admin/hosts/1/status")
                .contentType("application/json")
                .content("{\"active\":false,\"reason\":\"Vi phạm quy định\",\"note\":\"Khóa tạm thời\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRoomsWithoutInvoice() throws Exception {
        mockMvc.perform(get("/api/admin/rooms/without-invoice"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRevenueByMonth() throws Exception {
        mockMvc.perform(get("/api/admin/revenue?period=month"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRevenueByQuarter() throws Exception {
        mockMvc.perform(get("/api/admin/revenue?period=quarter"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRevenueByYear() throws Exception {
        mockMvc.perform(get("/api/admin/revenue?period=year"))
                .andExpect(status().isOk());
    }
}

