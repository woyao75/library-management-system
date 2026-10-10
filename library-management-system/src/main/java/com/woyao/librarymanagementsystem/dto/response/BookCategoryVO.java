package com.woyao.librarymanagementsystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookCategoryVO {

    private Integer categoryId;

    private String categoryName;

    private String description;

    private LocalDateTime createAt;

    private LocalDateTime updateTime;
}
