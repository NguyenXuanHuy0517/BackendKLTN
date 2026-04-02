package com.project.hostservice.service;

import com.project.datalayer.entity.MotelArea;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.Room;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.ContractServiceRepository;
import com.project.datalayer.repository.DepositRepository;
import com.project.datalayer.repository.RoomRepository;
import com.project.datalayer.repository.ServiceRepository;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.contract.ContractInviteCreateDTO;
import com.project.hostservice.dto.contract.ContractInviteResponseDTO;
import com.project.hostservice.mapper.ContractMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ContractManagementServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ContractServiceRepository contractServiceRepository;

    @Mock
    private ContractMapper contractMapper;

    @Mock
    private ContractInvitationTokenService contractInvitationTokenService;

    @Mock
    private Authentication authentication;

    private ContractManagementService contractManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contractManagementService = new ContractManagementService(
                contractRepository,
                roomRepository,
                userRepository,
                depositRepository,
                serviceRepository,
                contractServiceRepository,
                contractMapper,
                contractInvitationTokenService
        );
    }

    @Test
    void createContractInvitationReturnsSignedInviteForAvailableRoom() {
        User host = new User();
        host.setUserId(10L);
        host.setEmail("host@example.com");
        Role role = new Role();
        role.setRoleName("HOST");
        host.setRole(role);

        MotelArea area = new MotelArea();
        area.setAreaName("Khu A");
        area.setAddress("123 Duong ABC");
        area.setHost(host);

        Room room = new Room();
        room.setRoomId(5L);
        room.setRoomCode("P101");
        room.setStatus("AVAILABLE");
        room.setArea(area);

        ContractInviteCreateDTO request = new ContractInviteCreateDTO();
        request.setRoomId(5L);
        request.setStartDate(LocalDate.of(2026, 4, 10));
        request.setEndDate(LocalDate.of(2026, 10, 10));
        request.setActualRentPrice(new BigDecimal("3500000"));
        request.setElecPriceOverride(new BigDecimal("4000"));
        request.setWaterPriceOverride(new BigDecimal("18000"));
        request.setPenaltyTerms("Bao truoc 30 ngay");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("host@example.com");
        when(userRepository.findByEmail("host@example.com")).thenReturn(Optional.of(host));
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));
        when(contractInvitationTokenService.issueInvitation(host, room, request))
                .thenReturn(new ContractInvitationTokenService.IssuedInvitation(
                        "signed-invite",
                        LocalDateTime.of(2026, 4, 5, 9, 0)
                ));

        ContractInviteResponseDTO response = contractManagementService.createContractInvitation(
                request,
                authentication
        );

        assertEquals("signed-invite", response.getInviteCode());
        assertEquals("P101", response.getRoomCode());
        assertEquals("Khu A", response.getAreaName());
        assertEquals("123 Duong ABC", response.getAreaAddress());
        assertEquals(new BigDecimal("3500000"), response.getActualRentPrice());
    }

    @Test
    void createContractInvitationRejectsUnavailableRoom() {
        User host = new User();
        host.setUserId(10L);
        host.setEmail("host@example.com");
        Role role = new Role();
        role.setRoleName("HOST");
        host.setRole(role);

        MotelArea area = new MotelArea();
        area.setHost(host);

        Room room = new Room();
        room.setRoomId(5L);
        room.setRoomCode("P101");
        room.setStatus("RENTED");
        room.setArea(area);

        ContractInviteCreateDTO request = new ContractInviteCreateDTO();
        request.setRoomId(5L);
        request.setStartDate(LocalDate.of(2026, 4, 10));
        request.setEndDate(LocalDate.of(2026, 10, 10));
        request.setActualRentPrice(new BigDecimal("3500000"));

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("host@example.com");
        when(userRepository.findByEmail("host@example.com")).thenReturn(Optional.of(host));
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));

        assertThrows(
                IllegalStateException.class,
                () -> contractManagementService.createContractInvitation(request, authentication)
        );
    }
}
