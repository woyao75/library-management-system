package com.woyao.librarymanagementsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 基于 JWT Token 的门禁与权限管理系统
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    // 配置翻译官先生和客服小姐姐
    private final DatabaseJwtAuthenticationConverter authenticationConverter;
    private final SecurityErrorHandler securityErrorHandler;

    @Bean
    public SecurityFilterChain securityFilerChain(HttpSecurity http) throws Exception{

        http.csrf(csrf -> csrf.disable());
        // 安保策略（不认脸，只认票）
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 门禁规则
        http.authorizeHttpRequests(rules -> {
            rules.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
            rules.requestMatchers(HttpMethod.GET, "/books/**", "/categories/**").authenticated();
            rules.requestMatchers("/borrows/**").authenticated();
            rules.requestMatchers("/books/**", "/categories/**", "/admin/**").hasRole("ADMIN");
            rules.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                    "/doc.html", "/webjars/**").permitAll();
            rules.anyRequest().denyAll();
        });

        // 客服小姐姐处理
        http.exceptionHandling(errors -> {
            errors.authenticationEntryPoint(securityErrorHandler);
            errors.accessDeniedHandler(securityErrorHandler);
        });

        // 翻译官先生验证并翻译身份
        http.oauth2ResourceServer(resourceServer -> {
            resourceServer.authenticationEntryPoint(securityErrorHandler);
            resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter));
        });

        return http.build();
    }
}
