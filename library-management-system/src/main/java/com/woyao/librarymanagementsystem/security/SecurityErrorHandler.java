package com.woyao.librarymanagementsystem.security;

import com.woyao.librarymanagementsystem.common.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;

@Component
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    // 将 java 对象转换为 JSON 字符串的工具
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    // 保安 AuthenticationEntryPoint 拦住陌生访客并处理报错
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        writeError(response, 401,"请先登录，或重新获得有效 Token");
    }

    // 保安 AccessDeniedHandler 将普通用户拦在 VIP 门口并处理报错
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        writeError(response, 403, "没有操作权限");
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        // 1. 设置 HTTP 状态码
        response.setStatus(status);
        // 2. 告诉前端：我传入的是 Json 格式数据
        response.setContentType("application/json;charset=UTF-8");
        // 3. 构建统一返回对象
        Result<Void> result = Result.error(status, message);
        // 4. 将 Java 对象变成 Json 字符串
        String responseBody = jsonMapper.writeValueAsString(result);
        // 5. 将 Json 字符串写入响应，传给前端
        response.getWriter().write(responseBody);
    }
}
