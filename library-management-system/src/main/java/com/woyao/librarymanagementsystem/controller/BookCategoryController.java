package com.woyao.librarymanagementsystem.controller;

import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.common.result.Result;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryQueryDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryUpdateDTO;
import com.woyao.librarymanagementsystem.dto.response.BookCategoryVO;
import com.woyao.librarymanagementsystem.service.BookCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "图书分类管理")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody BookCategoryAddDTO dto) {
        bookCategoryService.add(dto);
        return Result.success();
    }

    @Operation(summary = "修改分类")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Integer categoryId, @Valid @RequestBody BookCategoryUpdateDTO dto) {
        bookCategoryService.update(categoryId, dto);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Integer categoryId) {
        bookCategoryService.delete(categoryId);
        return Result.success();
    }

    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public Result<PageResult<BookCategoryVO>> page(@Valid @ModelAttribute BookCategoryQueryDTO dto) {
        return Result.success(bookCategoryService.page(dto));
    }

    @Operation(summary = "查询分类列表")
    @GetMapping("/list")
    public Result<List<BookCategoryVO>> list() {
        return Result.success(bookCategoryService.list());
    }
}
