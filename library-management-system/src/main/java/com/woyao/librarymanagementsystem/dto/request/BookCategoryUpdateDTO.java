package com.woyao.librarymanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookCategoryUpdateDTO {

    @Schema(description = "分类名称", example = "计算机")
    @NotBlank(message = "分类名称不能为空")
    @Size(message = "分类名称不能超过50个字符")
    private String categoryName;

    @Schema(description = "分类描述")
    @Size(message = "分类描述不能超过500个字符")
    private String description;
}
