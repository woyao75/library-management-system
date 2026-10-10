package com.woyao.librarymanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookCategoryQueryDTO {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private long current = 1;

    @Schema(description = "每页条数")
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private long size = 1;

    @Schema(description = "分类名称")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String categoryName;
}
