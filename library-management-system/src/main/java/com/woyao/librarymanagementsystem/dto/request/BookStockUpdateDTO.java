package com.woyao.librarymanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "图书库存调整参数")
public class BookStockUpdateDTO {

    @Schema(description = "调整后的总库存，允许为0", example = "12")
    @NotNull(message = "总库存不能为空")
    @Min(value = 0, message = "总库存不能小于0")
    private Integer totalCopies;
}
