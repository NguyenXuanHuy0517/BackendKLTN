package com.project.authservice.service;

import com.project.authservice.dto.*;
import com.project.authservice.exception.ResourceNotFoundException;
import com.project.authservice.security.JwtUtils;
import com.project.datalayer.entity.Role;
import com.project.datalayer.entity.User;
import com.project.datalayer.repository.RoleRepository;
import com.project.datalayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    
    // Simple in-memory token blacklist (use Redis/DB in production)
    private static final Set<String> tokenBlacklist = new HashSet<>();
    private static final Map<String, String> passwordResetTokens = new HashMap<>();

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

    public void register(RegisterRequestDTO request) {
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
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);
    }

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

    public void logout(String token) {
        tokenBlacklist.add(token);
    }

    public void forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        // Generate reset token (in production use proper JWT or secure random token)
        String resetToken = UUID.randomUUID().toString();
        passwordResetTokens.put(resetToken, user.getEmail());
        
        // TODO: Send email with reset token
        // emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(ResetPasswordRequestDTO request) {
        String email = passwordResetTokens.get(request.getToken());
        if (email == null) {
            throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Remove used token
        passwordResetTokens.remove(request.getToken());
    }
}

