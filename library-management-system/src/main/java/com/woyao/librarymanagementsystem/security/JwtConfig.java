package com.woyao.librarymanagementsystem.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class JwtConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey jwtKey(@Value("${security.jwt.secret}") String secretText){
        // 1. 将密钥转换为字节。HS256要求密钥至少为 32 字节。
        byte[] secretBytes = secretText.getBytes(StandardCharsets.UTF_8);
        if(secretBytes.length < 32){
            throw new IllegalArgumentException("secret length less than 32");
        }
        // 2. 创建用于签名的密钥对象
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey){
        // 3. 将编码交给 Spring 管理。AuthService 可以注入这个Bean。
        ImmutableSecret<com.nimbusds.jose.proc.SecurityContext> secretSource =
                new ImmutableSecret<>(secretKey);
        return new NimbusJwtEncoder(secretSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // 检查签名是否正确
        // 检查 Token 是否过期，是否由本应用签发
        JwtTimestampValidator timeValidator = new JwtTimestampValidator(Duration.ZERO);
        JwtIssuerValidator issuerValidator = new JwtIssuerValidator("library-management-system");
        DelegatingOAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> validators =
                new DelegatingOAuth2TokenValidator<>(timeValidator, issuerValidator);
        decoder.setJwtValidator(validators);
        return decoder;
    }
}
