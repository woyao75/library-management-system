package com.woyao.librarymanagementsystem.security;

import com.woyao.librarymanagementsystem.entity.SysUser;
import com.woyao.librarymanagementsystem.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
    private final SysUserMapper sysUserMapper;

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        // 1. 读取 已经通过验签 Token 中的用户ID
        int userId;
        try{
            userId = Integer.parseInt(jwt.getSubject());
        }catch(NumberFormatException e){
            throw new OAuth2AuthenticationException("invalid user id");
        }

        // 2. 确认数据库中用户状态
        SysUser sysUser = sysUserMapper.selectById(userId);
        if(sysUser == null){
            throw new OAuth2AuthenticationException("invalid_token");
        }
        if(sysUser.getStatus() != 1){
            throw new OAuth2AuthenticationException("invalid_token");
        }

        // 3. 读取当前角色
        String authorityName;
        if(sysUser.getRole() == 1){
            authorityName = "ROLE_ADMIN";
        }else{
            authorityName = "ROLE_USER";
        }

        // 4. 将用户 ID 和角色装进JwtAuthenticationToken,交给Spring Security
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authorityName));
        String principalName = String.valueOf(userId);
        return new JwtAuthenticationToken(jwt, authorities, principalName);

    }
}
