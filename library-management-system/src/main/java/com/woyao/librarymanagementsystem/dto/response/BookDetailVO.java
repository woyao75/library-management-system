package com.woyao.librarymanagementsystem.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDetailVO {

    private Integer bookId;

    private String isbn;

    private String title;

    private String author;

    private String publisher;

    private LocalDate publishDate;

    private Integer categoryId;

    private Integer price;

    private String description;

    private String coverUrl;

    private Integer totalCopies;

    private Integer availableCopies;

    private String location;

}
