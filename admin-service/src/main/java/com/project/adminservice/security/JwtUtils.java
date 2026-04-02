package com.project.adminservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Vai trò: Thành phần bảo mật của module admin-service.
 * Chức năng: Xử lý các logic xác thực, phân quyền hoặc hỗ trợ bảo mật cho jwt.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

        /**
     * Chức năng: Tạo token.
     */
public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

        /**
     * Chức năng: Trích xuất email.
     */
public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

        /**
     * Chức năng: Kiểm tra token.
     */
public boolean validateToken(String token, UserDetails userDetails) {
        return extractEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ is token expired.
     */
private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

        /**
     * Chức năng: Trích xuất claim.
     */
private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

        /**
     * Chức năng: Lấy dữ liệu sign key.
     */
private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
