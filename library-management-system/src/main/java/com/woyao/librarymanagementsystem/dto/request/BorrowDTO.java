package com.woyao.librarymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BorrowDTO {

    @NotNull(message = "图书 ID 不能为空")
    @Positive(message = "图书 ID 必须大于0")
    private Integer bookId;

}
