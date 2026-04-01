package com.project.authservice.service;

import com.project.authservice.dto.*;
import com.project.authservice.exception.ResourceNotFoundException;
import com.project.authservice.security.JwtUtils;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.User;
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

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final Optional<EmailService> emailService;
    
    @Value("${email.send-reset-password:false}")
    private boolean sendPasswordResetEmail;

    // Simple in-memory token blacklist (use Redis/DB in production)
    private static final Set<String> tokenBlacklist = new HashSet<>();
    private static final Map<String, String> passwordResetTokens = new HashMap<>();

    /**
     * Đăng nhập người dùng
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Mật khẩu không đúng");
        }

        if (!user.isActive()) {
            throw new BadCredentialsException("Tài khoản đã bị khóa");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        return new LoginResponseDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                token
        );
    }

    /**
     * Đăng ký tài khoản tenant
     */
    public void registerTenant(RegisterRequestDTO request) {
        validateUserRegistration(request);
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        Role role = roleRepository.findByRoleName("TENANT")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role TENANT"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);
        log.info("Tenant registered successfully: {}", request.getEmail());

        // Send welcome email if available
        if (emailService.isPresent()) {
            try {
                emailService.get().sendWelcomeEmail(user.getEmail(), user.getFullName(), "TENANT");
            } catch (Exception e) {
                log.warn("Failed to send welcome email to: {}", user.getEmail(), e);
            }
        }
    }

    /**
     * Đăng ký tài khoản host
     */
    public void registerHost(RegisterRequestDTO request) {
        validateUserRegistration(request);
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        Role role = roleRepository.findByRoleName("HOST")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role HOST"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);
        log.info("Host registered successfully: {}", request.getEmail());

        // Send welcome email if available
        if (emailService.isPresent()) {
            try {
                emailService.get().sendWelcomeEmail(user.getEmail(), user.getFullName(), "HOST");
            } catch (Exception e) {
                log.warn("Failed to send welcome email to: {}", user.getEmail(), e);
            }
        }
    }

    /**
     * Validate thông tin đăng ký
     */
    private void validateUserRegistration(RegisterRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đầy đủ không được để trống");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
    }

    /**
     * Làm mới token
     */
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        if (tokenBlacklist.contains(request.getToken())) {
            throw new BadCredentialsException("Token đã bị hủy");
        }

        String email = jwtUtils.extractEmail(request.getToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        if (!jwtUtils.validateToken(request.getToken(), userDetails)) {
            throw new BadCredentialsException("Token không hợp lệ");
        }

        String newToken = jwtUtils.generateToken(userDetails);
        return new RefreshTokenResponseDTO(newToken);
    }

    /**
     * Đăng xuất người dùng
     */
    public void logout(String token) {
        tokenBlacklist.add(token);
        log.info("Token added to blacklist - User logged out");
    }

    /**
     * Yêu cầu reset mật khẩu
     */
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        // Generate reset token (in production use proper JWT or secure random token with expiration)
        String resetToken = UUID.randomUUID().toString();
        passwordResetTokens.put(resetToken, user.getEmail());

        log.info("Password reset token generated for: {}", user.getEmail());

        // Send email if enabled and available
        if (sendPasswordResetEmail) {
            emailService.ifPresentOrElse(
                service -> {
                    try {
                        service.sendPasswordResetEmail(user.getEmail(), resetToken);
                        log.info("Password reset email sent to: {}", user.getEmail());
                    } catch (Exception e) {
                        log.error("Failed to send password reset email to: {}", user.getEmail(), e);
                    }
                },
                () -> log.warn("EmailService not available. Reset token: {} (use for testing)", resetToken)
            );
        } else {
            log.info("Email sending disabled. Reset token available: {} (use for testing)", resetToken);
        }
    }

    /**
     * Reset mật khẩu với token
     */
    public void resetPassword(ResetPasswordRequestDTO request) {
        String email = passwordResetTokens.get(request.getToken());
        if (email == null) {
            throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset successfully for: {}", email);

        // Remove used token
        passwordResetTokens.remove(request.getToken());
    }
}



