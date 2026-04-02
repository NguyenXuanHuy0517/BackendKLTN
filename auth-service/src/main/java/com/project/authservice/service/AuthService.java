package com.project.authservice.service;

import com.project.authservice.dto.ForgotPasswordRequestDTO;
import com.project.authservice.dto.LoginRequestDTO;
import com.project.authservice.dto.LoginResponseDTO;
import com.project.authservice.dto.RefreshTokenRequestDTO;
import com.project.authservice.dto.RefreshTokenResponseDTO;
import com.project.authservice.dto.RegisterRequestDTO;
import com.project.authservice.dto.ResetPasswordRequestDTO;
import com.project.authservice.exception.ResourceNotFoundException;
import com.project.authservice.security.JwtUtils;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.ContractRepository;
import com.project.datalayer.repository.RoleRepository;
import com.project.datalayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final Set<String> TOKEN_BLACKLIST = new HashSet<>();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ContractRepository contractRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final Optional<EmailService> emailService;

    @Value("${email.send-reset-password:false}")
    private boolean sendPasswordResetEmail;

    @Value("${auth.reset-token.expiration-minutes:60}")
    private long resetTokenExpirationMinutes;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email khong ton tai"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Mat khau khong dung");
        }

        if (!user.isActive()) {
            throw new BadCredentialsException("Tai khoan da bi khoa");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        boolean requiresRentalJoin = "TENANT".equalsIgnoreCase(user.getRole().getRoleName())
                && contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(
                        user.getUserId(),
                        "ACTIVE"
                ).isEmpty();

        return new LoginResponseDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                token,
                requiresRentalJoin
        );
    }

    public void registerTenant(RegisterRequestDTO request) {
        validateUserRegistration(request);
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email da ton tai");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("So dien thoai da ton tai");
        }

        Role role = roleRepository.findByRoleName("TENANT")
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay role TENANT"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);

        emailService.ifPresent(service -> {
            try {
                service.sendWelcomeEmail(user.getEmail(), user.getFullName(), "TENANT");
            } catch (Exception e) {
                log.warn("Failed to send welcome email to {}", user.getEmail(), e);
            }
        });
    }

    public void registerHost(RegisterRequestDTO request) {
        validateUserRegistration(request);
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email da ton tai");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("So dien thoai da ton tai");
        }

        Role role = roleRepository.findByRoleName("HOST")
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay role HOST"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);

        emailService.ifPresent(service -> {
            try {
                service.sendWelcomeEmail(user.getEmail(), user.getFullName(), "HOST");
            } catch (Exception e) {
                log.warn("Failed to send welcome email to {}", user.getEmail(), e);
            }
        });
    }

    private void validateUserRegistration(RegisterRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email khong duoc de trong");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mat khau khong duoc de trong");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ten day du khong duoc de trong");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("So dien thoai khong duoc de trong");
        }
    }

    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        if (TOKEN_BLACKLIST.contains(request.getToken())) {
            throw new BadCredentialsException("Token da bi huy");
        }

        String email = jwtUtils.extractEmail(request.getToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtUtils.validateToken(request.getToken(), userDetails)) {
            throw new BadCredentialsException("Token khong hop le");
        }

        return new RefreshTokenResponseDTO(jwtUtils.generateToken(userDetails));
    }

    public void logout(String token) {
        TOKEN_BLACKLIST.add(token);
        log.info("Token added to blacklist - user logged out");
    }

    public void forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email khong ton tai"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes));
        userRepository.save(user);

        if (sendPasswordResetEmail) {
            emailService.ifPresentOrElse(
                    service -> {
                        try {
                            service.sendPasswordResetEmail(user.getEmail(), resetToken);
                        } catch (Exception e) {
                            log.error("Failed to send password reset email to {}", user.getEmail(), e);
                        }
                    },
                    () -> log.warn("EmailService not available. Reset token: {} (use for testing)", resetToken)
            );
        } else {
            log.info("Email sending disabled. Reset token available: {} (use for testing)", resetToken);
        }
    }

    public void resetPassword(ResetPasswordRequestDTO request) {
        User user = userRepository.findByResetTokenAndResetTokenExpiryAfter(
                        request.getToken(),
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new BadCredentialsException("Token khong hop le hoac da het han"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
