package com.woyao.librarymanagementsystem.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey jwtKey(@Value("${security.jwt.secret}") String secretText) {
        // 1. 将密钥转换为字节。HS256 要求密钥至少为 32 字节。
        byte[] secretBytes = secretText.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET 至少需要 32 字节");
        }

        // 2. 创建用于签名的密钥对象。
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        // 3. 将编码器交给 Spring 管理。AuthService 可以注入这个 Bean。
        ImmutableSecret<com.nimbusds.jose.proc.SecurityContext> secretSource =
                new ImmutableSecret<>(secretKey);
        return new NimbusJwtEncoder(secretSource);
    }
}
