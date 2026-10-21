package com.woyao.librarymanagementsystem.controller;

import com.woyao.librarymanagementsystem.common.result.Result;
import com.woyao.librarymanagementsystem.dto.request.LoginDTO;
import com.woyao.librarymanagementsystem.dto.response.LoginVO;
import com.woyao.librarymanagementsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        LoginVO loginVO = authService.login(username, password);

        return Result.success(loginVO);
    }
}
