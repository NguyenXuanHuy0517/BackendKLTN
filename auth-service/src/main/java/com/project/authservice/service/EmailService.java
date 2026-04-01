package com.project.authservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnBean(JavaMailSender.class)
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gửi email reset mật khẩu
     * @param toEmail Email của người dùng
     * @param resetToken Token dùng để reset mật khẩu
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[SmartRoom] Đặt lại mật khẩu");
            message.setText(buildResetPasswordEmailContent(resetToken));
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email reset password: " + e.getMessage());
        }
    }

    /**
     * Gửi email xác nhận đăng ký tài khoản
     * @param toEmail Email của người dùng
     * @param fullName Tên người dùng
     * @param userType Loại tài khoản (TENANT/HOST/ADMIN)
     */
    @SuppressWarnings("unused")
    public void sendWelcomeEmail(String toEmail, String fullName, String userType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[SmartRoom] Chào mừng bạn đến với SmartRoom");
            message.setText(buildWelcomeEmailContent(fullName, userType));
            
            mailSender.send(message);
            log.info("Welcome email sent to: {} ({})", toEmail, userType);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            // Không throw exception để không ảnh hưởng đến registration flow
            log.warn("Welcome email sending failed, but registration will continue");
        }
    }

    /**
     * Gửi email thông báo đăng nhập từ thiết bị mới
     * @param toEmail Email của người dùng
     * @param fullName Tên người dùng
     * @param loginTime Thời gian đăng nhập
     * @param device Thiết bị đăng nhập
     */
    @SuppressWarnings("unused")
    public void sendLoginNotificationEmail(String toEmail, String fullName, String loginTime, String device) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[SmartRoom] Thông báo đăng nhập mới");
            message.setText(buildLoginNotificationContent(fullName, loginTime, device));
            
            mailSender.send(message);
            log.info("Login notification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send login notification email to: {}", toEmail, e);
            // Không throw exception
        }
    }

    /**
     * Build nội dung email reset mật khẩu
     */
    private String buildResetPasswordEmailContent(String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        return String.format("""
            Xin chào,
            
            Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản SmartRoom của bạn.
            Vui lòng nhấp vào link bên dưới để tiếp tục:
            
            %s
            
            ⏰ Link này sẽ hết hạn trong 24 giờ.
            
            ⚠️ Nếu bạn không yêu cầu reset mật khẩu, vui lòng bỏ qua email này.
            Tài khoản của bạn vẫn an toàn.
            
            ---
            SmartRoom Team
            support@smartroom.com
            """, resetLink);
    }

    /**
     * Build nội dung email chào mừng
     */
    private String buildWelcomeEmailContent(String fullName, String userType) {
        String roleText = switch (userType.toUpperCase()) {
            case "HOST" -> "Quản lý motel/phòng trọ";
            case "TENANT" -> "Tìm kiếm phòng trọ";
            case "ADMIN" -> "Quản trị hệ thống";
            default -> "Sử dụng SmartRoom";
        };

        return String.format("""
            Xin chào %s,
            
            Chào mừng bạn đến với SmartRoom! 🎉
            
            Tài khoản của bạn đã được tạo thành công với vai trò: %s
            
            🚀 Bước tiếp theo:
            - Đăng nhập vào ứng dụng bằng email và mật khẩu
            - Hoàn thành thông tin hồ sơ cá nhân
            - Khám phá các tính năng của SmartRoom
            
            📱 Cài đặt ứng dụng:
            - Android: [Link to App Store]
            - iOS: [Link to Play Store]
            - Web: %s
            
            ❓ Cần trợ giúp? Liên hệ chúng tôi tại: support@smartroom.com
            
            ---
            SmartRoom Team
            """, fullName, roleText, frontendUrl);
    }

    /**
     * Build nội dung email thông báo đăng nhập
     */
    private String buildLoginNotificationContent(String fullName, String loginTime, String device) {
        return String.format("""
            Xin chào %s,
            
            Tài khoản SmartRoom của bạn vừa được đăng nhập từ một thiết bị mới:
            
            📱 Thiết bị: %s
            ⏰ Thời gian: %s
            
            Nếu đây không phải bạn, vui lòng đổi mật khẩu ngay:
            %s/change-password
            
            ---
            SmartRoom Team
            """, fullName, device, loginTime, frontendUrl);
    }
}





