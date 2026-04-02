package com.project.authservice.service;

import com.project.authservice.dto.LoginRequestDTO;
import com.project.authservice.dto.LoginResponseDTO;
import com.project.authservice.security.JwtUtils;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.RoleRepository;
import com.project.datalayer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetails userDetails;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(
                userRepository,
                roleRepository,
                contractRepository,
                passwordEncoder,
                jwtUtils,
                userDetailsService,
                authenticationManager,
                Optional.empty()
        );
    }

    @Test
    void loginMarksTenantWithoutActiveContractAsRequiringRentalJoin() {
        Role tenantRole = new Role();
        tenantRole.setRoleName("TENANT");

        User user = new User();
        user.setUserId(7L);
        user.setEmail("tenant@example.com");
        user.setPasswordHash("encoded");
        user.setRole(tenantRole);
        user.setActive(true);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("tenant@example.com");
        request.setPassword("secret");

        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("tenant@example.com")).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails)).thenReturn("jwt-token");
        when(contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(7L, "ACTIVE"))
                .thenReturn(Optional.empty());

        LoginResponseDTO response = authService.login(request);

        assertTrue(response.isRequiresRentalJoin());
    }
}
