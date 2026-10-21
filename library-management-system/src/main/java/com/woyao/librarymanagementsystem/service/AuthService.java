package com.woyao.librarymanagementsystem.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.woyao.librarymanagementsystem.common.exception.BusinessException;
import com.woyao.librarymanagementsystem.dto.response.LoginVO;
import com.woyao.librarymanagementsystem.entity.SysUser;
import com.woyao.librarymanagementsystem.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String ISSUER = "library-management-system";

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.ttl-seconds:3600}")
    private long tokenLifetimeSeconds;

    public LoginVO login(String username, String password){
        // 1. 校验BCrypt密码长度不能超过 72 字节
        int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;

        if(passwordBytes > 72){
            throw new BusinessException(400, "密码 UTF-8 长度不能超过 72 字节");
        }

        // 2. 按用户名查用户
        SysUser sysUser = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUserName, username.strip())
        );

        if(sysUser == null){
            throw new BadCredentialsException("用户名或密码错误");
        }

        boolean enabled = Integer.valueOf(1).equals(sysUser.getStatus());

        if(!enabled){
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 3. 比较明文密码与数据库中的哈希比较
        boolean passwordMatches = passwordEncoder.matches(password, sysUser.getPassword());
        if(!passwordMatches){
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 4. 生成带签名的 token
        String accessToken = createAccessToken(sysUser.getUserId());

        // 5. 返回登录结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(tokenLifetimeSeconds);
        return loginVO;
    }

    private String createAccessToken(int userId) {

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(tokenLifetimeSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(userId))
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        return jwtEncoder.encode(parameters).getTokenValue();

    }

}
