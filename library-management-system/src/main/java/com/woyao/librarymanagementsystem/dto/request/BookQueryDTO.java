package com.woyao.librarymanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BookQueryDTO {

    @Schema(description = "current默认为1")
    @Min(value = 1, message = "页码不能小于1")
    private long current = 1;

    @Schema(description = "size默认为1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Min(value = 1, message = "每页条数不能小于1")
    private long size = 10;

    private String title;

    private String author;

    private String isbn;

    private Integer categoryId;
}
