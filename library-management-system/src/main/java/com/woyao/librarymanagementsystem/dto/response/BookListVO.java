package com.woyao.librarymanagementsystem.dto.response;

import lombok.Data;

@Data
public class BookListVO {
    private Integer bookId;

    private String isbn;

    private String title;

    private String author;

    private Integer categoryId;

    private String coverUrl;

    private Integer price;

    private Integer availableCopies;
}
