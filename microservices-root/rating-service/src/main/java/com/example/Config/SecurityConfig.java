package com.example.Config;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(auth -> auth
                        .requestMatchers("/actuator/prometheus", "/actuator/health/**",
                                "/swagger-ui", "/swagger-ui/**", "/error", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/storefront/**").permitAll()
                        .requestMatchers("/backoffice/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> {})
        )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
