package com.project.adminservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(c -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOriginPatterns(java.util.Arrays.asList("*"));
                    config.setAllowedMethods(java.util.Arrays.asList("*"));
                    config.setAllowedHeaders(java.util.Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/admin/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/admin/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

