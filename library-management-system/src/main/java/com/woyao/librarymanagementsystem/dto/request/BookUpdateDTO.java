package com.woyao.librarymanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookUpdateDTO {

    @NotBlank(message = "国际标准书号不能为空")
    @Size(max = 20, message = "国际标准书号不能超过20个字符")
    private String isbn;

    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名不能超过200个字符")
    private String title;

    @Size(max = 100, message = "作者不能超过100个字符")
    private String author;

    @Size(max = 100, message = "出版社不能超过100个字符")
    private String publisher;

    private LocalDate publishDate;

    @NotNull(message = "所属分类ID不能为空")
    @Positive(message = "所属分类ID必须大于0")
    private Integer categoryId;

    @NotNull(message = "价格不能为空")
    @Min(value = 1, message = "价格最小不能小于1")
    @Max(value = 1000, message = "价格最高不能大于1000")
    private Integer price;

    @Schema(description = "图书简介，null或未传时保留原值，空字符串清空")
    @Size(max = 500, message = "图书简介不能超过500个字符")
    private String description;

    @Size(max = 255, message = "封面图片储存路径不能超过255个字符")
    private String coverUrl;

    @Size(max = 50, message = "馆藏位置不能超过50个字符")
    private String location;
}
