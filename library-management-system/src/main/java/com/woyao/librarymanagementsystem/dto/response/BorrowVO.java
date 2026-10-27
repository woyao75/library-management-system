package com.woyao.librarymanagementsystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowVO {

    private Integer recordId;
    private Integer bookId;
    private Integer status;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

}
