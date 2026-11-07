package com.woyao.librarymanagementsystem.controller;

import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.common.result.Result;
import com.woyao.librarymanagementsystem.dto.response.BorrowVO;
import com.woyao.librarymanagementsystem.entity.BorrowRecord;
import com.woyao.librarymanagementsystem.service.BookCategoryService;
import com.woyao.librarymanagementsystem.service.impl.BorrowService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/borrows")
@RequiredArgsConstructor
public class AdminBorrowController {

    private final BorrowService borrowService;

    @GetMapping
    public Result<PageResult<BorrowVO>> page(
            @Positive int userId,
            @Positive int status,
            @RequestParam(defaultValue = "1") @Min(1) long current,
            @RequestParam(defaultValue = "10") @Min(1) long size){

        return Result.success(borrowService.pageAdmin(userId, status, current, size));
    }

    @GetMapping("/{id}")
    public Result<BorrowVO> detail(@PathVariable("id") @Positive int recordId){
        return Result.success(borrowService.detailAdmin(recordId));
    }

    @PostMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable("id") @Positive int recordId){
        borrowService.returnAsAdmin(recordId);
        return Result.success();
    }
}
