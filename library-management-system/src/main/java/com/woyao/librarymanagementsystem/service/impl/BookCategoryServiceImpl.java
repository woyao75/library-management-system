package com.woyao.librarymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.woyao.librarymanagementsystem.common.exception.BusinessException;
import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryQueryDTO;
import com.woyao.librarymanagementsystem.dto.request.BookCategoryUpdateDTO;
import com.woyao.librarymanagementsystem.dto.response.BookCategoryVO;
import com.woyao.librarymanagementsystem.entity.Book;
import com.woyao.librarymanagementsystem.entity.BookCategory;
import com.woyao.librarymanagementsystem.mapper.BookCategoryMapper;
import com.woyao.librarymanagementsystem.mapper.BookMapper;
import com.woyao.librarymanagementsystem.service.BookCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static javax.management.Query.eq;

@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryMapper bookCategoryMapper;
    private final BookMapper bookMapper;

    @Override
    public void add(BookCategoryAddDTO dto) {
        String categoryName = dto.getCategoryName().strip();

        checkNameExists(categoryName,null);

        BookCategory  category = new BookCategory();
        category.setCategoryName(categoryName);
        category.setDescription(dto.getDescription());
        category.setIsDeleted(0);

        int affectedRows = bookCategoryMapper.insert(category);

        if(affectedRows != 1) {
            throw new BusinessException("分类新增失败");
        }
    }

    @Override
    public void update(Integer categoryId, BookCategoryUpdateDTO dto) {
        checkBookExists(categoryId);

        String categoryName = dto.getCategoryName().strip();

        checkNameExists(categoryName,categoryId);

        BookCategory  category = new BookCategory();

        category.setCategoryId(categoryId);
        category.setCategoryName(categoryName);
        category.setDescription(dto.getDescription());

        int affectedRows = bookCategoryMapper.updateById(category);

        if(affectedRows == 0) {
            throw new BusinessException("分类更新失败");
        }
    }

    @Override
    public void delete(Integer categoryId) {
        checkBookExists(categoryId);

        Long count = bookMapper.selectCount(
                Wrappers.<Book>lambdaQuery()
                        .eq(Book::getCategoryId, categoryId)
        );

        if(count > 0) {
            throw new BusinessException("该分类下存在图书，不能删除");
        }

        int affectedRows = bookCategoryMapper.deleteById(categoryId);

        if(affectedRows == 0) {
            throw new BusinessException("该分类已被删除");
        }
    }

    @Override
    public List<BookCategoryVO> list() {
        List<BookCategory> categories = bookCategoryMapper.selectList(
                Wrappers.<BookCategory>lambdaQuery()
                        .orderByAsc(BookCategory::getCategoryId)
        );

        return categories.stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public PageResult<BookCategoryVO> page(BookCategoryQueryDTO dto) {
        Page<BookCategory> page = new Page<>(dto.getCurrent(),dto.getSize());

        String categoryName = StringUtils.hasText(dto.getCategoryName())
                ? dto.getCategoryName().strip()
                : null;

        Page<BookCategory> result = bookCategoryMapper.selectPage(
                page,
                Wrappers.<BookCategory>lambdaQuery()
                        .like(
                                categoryName != null,
                                BookCategory::getCategoryName,
                                categoryName
                        )
                        .orderByAsc(BookCategory::getCategoryId)
        );

        IPage<BookCategoryVO> voPage = result.convert(this::toVO);

        return PageResult.of(voPage);
    }

    private void checkNameExists(String categoryName, Integer id) {
        Long count = bookCategoryMapper.selectCount(
                Wrappers.<BookCategory>lambdaQuery()
                        .eq(BookCategory::getCategoryName, categoryName)
                        .ne(
                                id != null,
                                BookCategory::getCategoryId,
                                id
                        )
        );
        if (count > 0) {
            throw new BusinessException("分类名称已存在");
        }
    }

    private void checkBookExists(Integer id) {
        BookCategory category = bookCategoryMapper.selectById(id);

        if(category == null) {
            throw new BusinessException("分类不存在或已被删除");
        }
    }

    private BookCategoryVO toVO(BookCategory bookCategory) {
        BookCategoryVO bookCategoryVO = new BookCategoryVO();

        bookCategoryVO.setCategoryId(bookCategory.getCategoryId());
        bookCategoryVO.setCategoryName(bookCategory.getCategoryName());
        bookCategoryVO.setDescription(bookCategory.getDescription());
        bookCategoryVO.setCreateAt(bookCategory.getCreateAt());
        bookCategoryVO.setUpdateTime(bookCategory.getUpdateTime());

        return bookCategoryVO;
    }
}
