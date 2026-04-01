package com.project.authservice.controller;

import com.project.authservice.dto.*;
import com.project.authservice.service.AuthService;
import com.project.datalayer.dto.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng nhập người dùng
     * @param request Email và mật khẩu
     * @return Token JWT và thông tin người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            log.info("Login request for email: {}", request.getEmail());
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công"));
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Đăng ký tài khoản tenant (khách thuê phòng)
     * @param request Thông tin đăng ký
     * @return Thông báo thành công
     */
    @PostMapping("/register/tenant")
    public ResponseEntity<ApiResponse<Void>> registerTenant(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            log.info("Tenant registration request for email: {}", request.getEmail());
            authService.registerTenant(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(null, "Đăng ký tài khoản thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Đăng ký tài khoản host (chủ phòng trọ/motel)
     * @param request Thông tin đăng ký
     * @return Thông báo thành công
     */
    @PostMapping("/register/host")
    public ResponseEntity<ApiResponse<Void>> registerHost(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            log.info("Host registration request for email: {}", request.getEmail());
            authService.registerHost(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(null, "Đăng ký tài khoản thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Làm mới token JWT
     * @param request Refresh token hiện tại
     * @return Token JWT mới
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            log.info("Refresh token request");
            RefreshTokenResponseDTO response = authService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Token đã được làm mới"));
        } catch (Exception e) {
            log.error("Refresh token failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Đăng xuất người dùng
     * @param authHeader Authorization header chứa Bearer token
     * @return Thông báo thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Authorization header không hợp lệ"));
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            log.info("Logout request");
            authService.logout(token);
            return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công"));
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Yêu cầu reset mật khẩu
     * Gửi email chứa link reset password đến người dùng
     * @param request Email của người dùng
     * @return Thông báo thành công
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            log.info("Forgot password request for email: {}", request.getEmail());
            authService.forgotPassword(request);
            return ResponseEntity.ok(ApiResponse.success(null, 
                    "Yêu cầu reset mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn"));
        } catch (Exception e) {
            log.error("Forgot password failed: {}", e.getMessage());
            // Return generic message to prevent email enumeration attack
            return ResponseEntity.ok(ApiResponse.success(null, 
                    "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được hướng dẫn reset mật khẩu"));
        }
    }

    /**
     * Reset mật khẩu với token
     * @param request Token reset password và mật khẩu mới
     * @return Thông báo thành công
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        try {
            log.info("Reset password request");
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success(null, "Mật khẩu đã được thay đổi thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Reset password failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Debug endpoint - kiểm tra JWT secret (chỉ dùng khi phát triển)
     */
    @GetMapping("/debug/secret")
    public ResponseEntity<String> debugSecret(
            @org.springframework.beans.factory.annotation.Value("${jwt.secret}") String secret) {
        return ResponseEntity.ok("AUTH secret: [" + secret + "] length=" + secret.length());
    }
}


