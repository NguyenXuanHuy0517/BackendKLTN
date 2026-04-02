package com.project.tenantservice.service;

import com.project.datalayer.entity.Contract;
import com.project.datalayer.entity.MotelArea;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.Room;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.RoomRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.tenantservice.dto.contract.MyContractDTO;
import com.project.tenantservice.mapper.ContractMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RentalJoinServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractServiceRepository contractServiceRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractMapper contractMapper;

    @Mock
    private ContractInvitationTokenService contractInvitationTokenService;

    @Mock
    private Authentication authentication;

    private RentalJoinService rentalJoinService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rentalJoinService = new RentalJoinService(
                contractRepository,
                contractServiceRepository,
                roomRepository,
                userRepository,
                contractMapper,
                contractInvitationTokenService
        );
    }

    @Test
    void claimInvitationCreatesActiveContractAndRentsRoom() {
        User host = new User();
        host.setUserId(3L);

        User tenant = new User();
        tenant.setUserId(7L);
        tenant.setEmail("tenant@example.com");
        Role tenantRole = new Role();
        tenantRole.setRoleName("TENANT");
        tenant.setRole(tenantRole);

        MotelArea area = new MotelArea();
        area.setHost(host);
        area.setAreaName("Khu B");
        area.setAddress("456 Duong XYZ");

        Room room = new Room();
        room.setRoomId(12L);
        room.setRoomCode("P202");
        room.setStatus("AVAILABLE");
        room.setElecPrice(new BigDecimal("3500"));
        room.setWaterPrice(new BigDecimal("15000"));
        room.setArea(area);

        ContractInvitationTokenService.ContractInvitationPayload payload =
                new ContractInvitationTokenService.ContractInvitationPayload(
                        3L,
                        12L,
                        LocalDate.of(2026, 4, 15),
                        LocalDate.of(2026, 12, 15),
                        new BigDecimal("3200000"),
                        new BigDecimal("3900"),
                        new BigDecimal("17000"),
                        "Bao truoc 15 ngay",
                        LocalDateTime.of(2026, 4, 10, 8, 0)
                );

        MyContractDTO dto = new MyContractDTO();
        dto.setContractId(99L);
        dto.setContractCode("HD-99");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("tenant@example.com");
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(7L, "ACTIVE"))
                .thenReturn(Optional.empty());
        when(contractInvitationTokenService.parseInvitation("invite-code")).thenReturn(payload);
        when(roomRepository.findByIdForUpdate(12L)).thenReturn(Optional.of(room));
        when(contractRepository.findByRoom_RoomIdAndStatus(12L, "ACTIVE")).thenReturn(Optional.empty());
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> {
            Contract contract = invocation.getArgument(0);
            contract.setContractId(99L);
            return contract;
        });
        when(contractServiceRepository.findByContract_ContractId(99L)).thenReturn(List.of());
        when(contractMapper.toDTO(any(Contract.class), eq(List.of()))).thenReturn(dto);

        MyContractDTO result = rentalJoinService.claimInvitation("invite-code", authentication);

        assertSame(dto, result);
        assertEquals("RENTED", room.getStatus());
        verify(roomRepository).save(room);
        verify(contractRepository).save(any(Contract.class));
    }
}
