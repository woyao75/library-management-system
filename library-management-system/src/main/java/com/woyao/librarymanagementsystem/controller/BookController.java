package com.woyao.librarymanagementsystem.controller;


import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.common.result.Result;
import com.woyao.librarymanagementsystem.dto.request.BookAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookQueryDTO;
import com.woyao.librarymanagementsystem.dto.response.BookDetailVO;
import com.woyao.librarymanagementsystem.dto.response.BookListVO;
import com.woyao.librarymanagementsystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "图书管理")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @Operation(summary = "新增图书")
    @PostMapping
    public Result<Void> addBook(@Valid @RequestBody BookAddDTO dto) {
        bookService.add(dto);
        return Result.success();
    }

    @Operation(summary = "图书详情")
    @GetMapping("/{id}")
    public Result<BookDetailVO> getBook(@PathVariable("id") Integer bookId) {
        bookService.detail(bookId);
        return Result.success(bookService.detail(bookId));
    }

    @Operation(summary = "图书分页查询")
    @GetMapping("/books/page")
    public Result<PageResult<BookListVO>> page(@Valid @ModelAttribute BookQueryDTO dto){
        return Result.success(bookService.page(dto));
    }

}
