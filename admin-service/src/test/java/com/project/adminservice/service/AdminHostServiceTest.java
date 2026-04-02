package com.project.adminservice.service;

import com.project.adminservice.dto.host.AdminHostDetailDTO;
import com.project.adminservice.dto.host.AdminHostResponseDTO;
import com.project.adminservice.dto.host.AdminHostStatusUpdateRequest;
import com.project.adminservice.exception.ResourceNotFoundException;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminHostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MotelAreaRepository areaRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private AdminHostService adminHostService;

    private User testHost;
    private Role hostRole;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        hostRole = new Role();
        hostRole.setRoleName("HOST");

        testHost = new User();
        testHost.setUserId(1L);
        testHost.setFullName("Test Host");
        testHost.setEmail("host@test.com");
        testHost.setPhoneNumber("0123456789");
        testHost.setActive(true);
        testHost.setRole(hostRole);
    }

    @Test
    public void testGetHostDetailSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));
        when(areaRepository.countByHost_UserId(1L)).thenReturn(0L);
        when(roomRepository.countByArea_Host_UserId(1L)).thenReturn(0L);
        when(contractRepository.countByRoom_Area_Host_UserIdAndStatus(1L, "ACTIVE")).thenReturn(0L);
        when(invoiceRepository.countOverdueByHostId(1L)).thenReturn(0L);
        when(roomRepository.countRoomsWithoutInvoiceByHostId(1L, java.time.YearMonth.now().getMonthValue(), java.time.YearMonth.now().getYear())).thenReturn(0L);

        AdminHostDetailDTO detail = adminHostService.getHostDetail(1L);

        assertNotNull(detail);
        assertEquals("Test Host", detail.getFullName());
        assertEquals("host@test.com", detail.getEmail());
        assertTrue(detail.isActive());
    }

    @Test
    public void testGetAllHostsUsesAggregateCounts() {
        User secondHost = new User();
        secondHost.setUserId(2L);
        secondHost.setFullName("Second Host");
        secondHost.setEmail("second@test.com");
        secondHost.setPhoneNumber("0987654321");
        secondHost.setActive(false);
        secondHost.setRole(hostRole);

        when(userRepository.findByRole_RoleName("HOST")).thenReturn(List.of(testHost, secondHost));
        when(areaRepository.countAreasByHostIds(List.of(1L, 2L))).thenReturn(List.of(
                new Object[]{1L, 2L},
                new Object[]{2L, 1L}
        ));
        when(roomRepository.countRoomsByHostIds(List.of(1L, 2L))).thenReturn(List.of(
                new Object[]{1L, 5L},
                new Object[]{2L, 3L}
        ));

        List<AdminHostResponseDTO> result = adminHostService.getAllHosts();

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getTotalAreas());
        assertEquals(5L, result.get(0).getTotalRooms());
        assertEquals(1L, result.get(1).getTotalAreas());
        assertEquals(3L, result.get(1).getTotalRooms());
        verify(areaRepository).countAreasByHostIds(List.of(1L, 2L));
        verify(roomRepository).countRoomsByHostIds(List.of(1L, 2L));
        verify(areaRepository, never()).countByHost_UserId(anyLong());
        verify(roomRepository, never()).countByArea_Host_UserId(anyLong());
    }

    @Test
    public void testGetHostDetailNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminHostService.getHostDetail(1L);
        });
    }

    @Test
    public void testUpdateHostStatusSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));

        AdminHostStatusUpdateRequest request = new AdminHostStatusUpdateRequest();
        request.setActive(false);
        request.setReason("Vi phạm quy định");

        adminHostService.updateHostStatus(1L, request);

        verify(userRepository, times(1)).save(testHost);
        assertFalse(testHost.isActive());
    }

    @Test
    public void testUpdateHostStatusIdempotent() {
        // Host already inactive
        testHost.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));

        AdminHostStatusUpdateRequest request = new AdminHostStatusUpdateRequest();
        request.setActive(false);
        request.setReason("Already blocked");

        // Should not save if already in same state
        adminHostService.updateHostStatus(1L, request);

        // No change, so save should not be called (idempotent)
        verify(userRepository, never()).save(testHost);
    }

    @Test
    public void testUpdateNonHostUserFails() {
        Role tenantRole = new Role();
        tenantRole.setRoleName("TENANT");
        testHost.setRole(tenantRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));

        AdminHostStatusUpdateRequest request = new AdminHostStatusUpdateRequest();
        request.setActive(false);
        request.setReason("Test");

        assertThrows(IllegalArgumentException.class, () -> {
            adminHostService.updateHostStatus(1L, request);
        });
    }

    @Test
    public void testCannotLockAdminAccount() {
        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        testHost.setRole(adminRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));

        AdminHostStatusUpdateRequest request = new AdminHostStatusUpdateRequest();
        request.setActive(false);
        request.setReason("Test");

        assertThrows(IllegalArgumentException.class, () -> {
            adminHostService.updateHostStatus(1L, request);
        });
    }
}

