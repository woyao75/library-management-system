package com.woyao.librarymanagementsystem.dto.response;

import lombok.Data;

@Data
public class LoginVO {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}
