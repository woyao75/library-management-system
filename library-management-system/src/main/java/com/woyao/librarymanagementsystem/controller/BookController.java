package com.woyao.librarymanagementsystem.controller;


import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.common.result.Result;
import com.woyao.librarymanagementsystem.dto.request.BookAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookQueryDTO;
import com.woyao.librarymanagementsystem.dto.request.BookStockUpdateDTO;
import com.woyao.librarymanagementsystem.dto.request.BookUpdateDTO;
import com.woyao.librarymanagementsystem.dto.response.BookDetailVO;
import com.woyao.librarymanagementsystem.dto.response.BookListVO;
import com.woyao.librarymanagementsystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

    @Operation(summary = "更新图书")
    @PutMapping("/{id}")
    public Result<Void> updateBook(@PathVariable("id") Integer bookId, @Valid @RequestBody BookUpdateDTO dto) {
        bookService.update(bookId, dto);
        return Result.success();
    }

    @Operation(summary = "删除图书")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable("id")@Positive Integer bookId) {
        bookService.delete(bookId);
        return Result.success();
    }

    @Operation(summary = "调整馆藏总量")
    @PutMapping("/{id}")
    public Result<Void> adjustStock(@PathVariable("id") Integer bookId, @Valid @RequestBody BookStockUpdateDTO dto) {
        bookService.adjustStock(bookId, dto.getTotalCopies());
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
