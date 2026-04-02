package com.project.adminservice.service;

import com.project.adminservice.dto.room.AdminRoomAuditDTO;
import com.project.datalayer.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminRoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminRoomService adminRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoomsMapsAuditRows() {
        when(roomRepository.findAdminRoomAuditRows()).thenReturn(List.<Object[]>of(
                new Object[]{1L, "P101", "Khu A", "Host A", "RENTED", BigDecimal.valueOf(2500000), "Tenant A"},
                new Object[]{2L, "P102", "Khu B", "Host B", "AVAILABLE", BigDecimal.valueOf(2000000), null}
        ));

        List<AdminRoomAuditDTO> result = adminRoomService.getAllRooms();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getRoomId());
        assertEquals("P101", result.get(0).getRoomCode());
        assertEquals("Tenant A", result.get(0).getCurrentTenantName());
        assertNull(result.get(0).getDaysWithoutInvoice());
        assertEquals("AVAILABLE", result.get(1).getStatus());
        assertNull(result.get(1).getCurrentTenantName());
    }

    @Test
    void getRoomsMissingInvoicesMapsRowsWithComputedDays() {
        YearMonth currentMonth = YearMonth.now();
        when(roomRepository.findAdminRoomsMissingInvoiceRows(
                currentMonth.getMonthValue(),
                currentMonth.getYear()
        )).thenReturn(List.<Object[]>of(
                new Object[]{3L, "P201", "Khu C", "Host C", "RENTED", BigDecimal.valueOf(3000000), "Tenant C"}
        ));

        List<AdminRoomAuditDTO> result = adminRoomService.getRoomsMissingInvoices();

        long expectedDays = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), 1),
                LocalDate.now()
        );

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getRoomId());
        assertEquals("Tenant C", result.get(0).getCurrentTenantName());
        assertEquals(expectedDays, result.get(0).getDaysWithoutInvoice());
        verify(roomRepository).findAdminRoomsMissingInvoiceRows(
                currentMonth.getMonthValue(),
                currentMonth.getYear()
        );
    }
}
