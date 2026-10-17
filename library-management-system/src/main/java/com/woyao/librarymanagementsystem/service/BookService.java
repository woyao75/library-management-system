package com.woyao.librarymanagementsystem.service;

import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.dto.request.BookAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookQueryDTO;
import com.woyao.librarymanagementsystem.dto.response.BookDetailVO;
import com.woyao.librarymanagementsystem.dto.response.BookListVO;
import com.woyao.librarymanagementsystem.entity.Book;

import java.util.List;

public interface BookService {
    void add(BookAddDTO dto);

    BookDetailVO detail(Integer bookId);

    PageResult<BookListVO> page(BookQueryDTO dto);

}
