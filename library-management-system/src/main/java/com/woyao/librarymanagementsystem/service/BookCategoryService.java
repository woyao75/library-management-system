package com.woyao.librarymanagementsystem.service;


import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryQueryDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryUpdateDTO;
import com.woyao.librarymanagementsystem.dto.response.BookCategoryVO;
import com.woyao.librarymanagementsystem.entity.BookCategory;

import java.util.List;

public interface BookCategoryService {

    void add(BookCategoryAddDTO dto);

    void update(Integer categoryId, BookCategoryUpdateDTO dto);

    void delete(Integer categoryId);

    List<BookCategoryVO> list();

    PageResult<BookCategoryVO> page(BookCategoryQueryDTO dto);
}
