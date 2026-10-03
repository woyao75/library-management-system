package com.woyao.librarymanagementsystem.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("book")
public class Book {

    @TableId(value = "book_id", type = IdType.AUTO)
    private Integer bookId;

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDateTime    publishedDate;
    private Integer categoryId;
    private Integer price;
    private String description;
    private String coverUrl;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;

    @TableField(value = "create_at", fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
