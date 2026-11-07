package com.woyao.librarymanagementsystem.controller;

import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.common.result.Result;
import com.woyao.librarymanagementsystem.dto.request.BorrowDTO;
import com.woyao.librarymanagementsystem.dto.response.BorrowVO;
import com.woyao.librarymanagementsystem.service.impl.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "借阅记录")
@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
public class BorrowController {
    private final BorrowService borrowService;

    @Operation(summary = "用户借阅")
    @PostMapping
    public Result<Integer> borrow(Authentication authentication, @Valid @RequestBody BorrowDTO dto){
        int bookId = dto.getBookId();
        int userId = getCurrentUserId(authentication);

        int recordId = borrowService.borrow(userId, bookId);

        return Result.success(recordId);
    }

    @Operation(summary = "借阅详情")
    @GetMapping("/{id}")
    public Result<BorrowVO> detail(Authentication authentication, @PathVariable("id") @Positive int recordId){
        int userId = getCurrentUserId(authentication);

        BorrowVO result = borrowService.detail(userId, recordId);
        return Result.success(result);
    }

    @Operation(summary = "分页查询借阅记录")
    @GetMapping("/me")
    public Result<PageResult<BorrowVO>> mine(Authentication authentication,
                                             @RequestParam(defaultValue = "1") @Min(1) long current,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) long size){
            int userId = getCurrentUserId(authentication);
            PageResult<BorrowVO> pageResult = borrowService.pageMine(userId, current, size);

            return Result.success(pageResult);

    }

    @Operation(summary = "归还图书")
    @PostMapping("/{id}/return")
    public Result<Void> returnBook(Authentication authentication, @PathVariable("id") @Positive int recordId){
        int userId = getCurrentUserId(authentication);

        borrowService.returnBook(userId, recordId);

        return Result.success();
    }

    private int getCurrentUserId(Authentication authentication) {
        String userIdText = authentication.getName();
        return Integer.valueOf(userIdText);
    }
}
