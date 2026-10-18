package com.woyao.librarymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.woyao.librarymanagementsystem.common.exception.BusinessException;
import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.dto.request.BookAddDTO;
import com.woyao.librarymanagementsystem.dto.request.BookQueryDTO;
import com.woyao.librarymanagementsystem.dto.request.BookUpdateDTO;
import com.woyao.librarymanagementsystem.dto.response.BookDetailVO;
import com.woyao.librarymanagementsystem.dto.response.BookListVO;
import com.woyao.librarymanagementsystem.entity.Book;
import com.woyao.librarymanagementsystem.entity.BookCategory;
import com.woyao.librarymanagementsystem.entity.BorrowRecord;
import com.woyao.librarymanagementsystem.mapper.BookCategoryMapper;
import com.woyao.librarymanagementsystem.mapper.BookMapper;
import com.woyao.librarymanagementsystem.mapper.BorrowRecordMapper;
import com.woyao.librarymanagementsystem.mapper.SysUserMapper;
import com.woyao.librarymanagementsystem.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookCategoryMapper bookCategoryMapper;
    private final SysUserMapper sysUserMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    @Transactional
    public void add(BookAddDTO dto) {
        String name = dto.getTitle().strip();
        String isbn = dto.getIsbn().strip();

        checkCategoryExists(dto.getCategoryId());
        checkIsbnExists(isbn, dto.getCategoryId());

        Book book = new Book();
        book.setTitle(name);
        book.setIsbn(isbn);
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setPublishDate(dto.getPublishDate());
        book.setCategoryId(dto.getCategoryId());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());
        book.setCoverUrl(dto.getCoverUrl());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());
        book.setLocation(dto.getLocation());
        book.setIsDeleted(0);

        int affectRows = bookMapper.insert(book);

        if (affectRows != 1) {
            throw new BusinessException("图书新增失败");
        }
    }

    @Override
    @Transactional
    public void delete(Integer bookId) {
        checkBookExists(bookId);

        long count = borrowRecordMapper.selectCount(
                Wrappers.<BorrowRecord>lambdaQuery()
                        .eq(BorrowRecord::getBookId, bookId)
                        .eq(BorrowRecord::getStatus,0)
        );

        if(count > 0) {
            throw new BusinessException("该图书存在未归还的借阅记录，不能删除");
        }

        int affectRows = bookMapper.deleteById(bookId);

        if(affectRows != 1) {
            throw new BusinessException("该图书已被删除");
        }
    }

    @Override
    @Transactional
    public void update(Integer bookId, BookUpdateDTO dto) {
        checkCategoryExists(dto.getCategoryId());
        checkBookExists(bookId);

        String title = dto.getTitle().strip();
        String isbn = dto.getIsbn().strip();

        checkCategoryExists(dto.getCategoryId());

        checkIsbnExists(isbn, bookId);

        Book book = new Book();

        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(dto.getAuthor().strip());
        book.setPublisher(dto.getPublisher().strip());
        book.setPublishDate(dto.getPublishDate());
        book.setCategoryId(dto.getCategoryId());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());
        book.setCoverUrl(dto.getCoverUrl());
        book.setLocation(dto.getLocation().strip());

        int affectRows = bookMapper.updateById(book);

        if (affectRows == 0) {
            throw new BusinessException("图书更新失败");
        }
    }

    @Override
    public BookDetailVO detail(Integer bookId) {
        Book book = bookMapper.selectById(bookId);

        if(book == null){
            throw new BusinessException("图书不存在或已被删除");
        }

        return toVO(book);
    }

    @Override
    public PageResult<BookListVO> page(BookQueryDTO dto) {
        Page<Book> page = new Page<>(dto.getCurrent(), dto.getSize());

        String title = StringUtils.hasText(dto.getTitle())
                ?dto.getTitle().strip()
                :null;

        String author = StringUtils.hasText(dto.getAuthor())
                ?dto.getAuthor().strip()
                :null;

        String isbn = StringUtils.hasText(dto.getIsbn())
                ?dto.getIsbn().strip()
                :null;

        Page<Book> result = bookMapper.selectPage(
                page,
                Wrappers.<Book>lambdaQuery()
                        .like(
                                title != null,
                                Book::getTitle,
                                title
                        )
                        .like(
                                author != null,
                                Book::getAuthor,
                                author
                        )
                        .eq(
                                isbn != null,
                                Book::getIsbn,
                                isbn
                        )
                        .eq(
                                dto.getCategoryId() != null,
                                Book::getCategoryId,
                                dto.getCategoryId()
                        )
                        .orderByDesc(Book::getBookId)
        );

        IPage<BookListVO> voPage = result.convert(this::toListVO);

        return PageResult.of(voPage);
    }

    private void checkBookExists(Integer id) {
        Book book = bookMapper.selectById(id);

        if(book == null){
            throw new BusinessException("图书不存在或已被删除");
        }
    }

    private void checkCategoryExists(Integer id) {
        BookCategory category = bookCategoryMapper.selectById(id);

        if(category == null) {
            throw new BusinessException("分类不存在或已被删除");
        }
    }

    private void checkIsbnExists(String isbn, Integer bookId) {
        Long count = bookMapper.selectCount(
                Wrappers.<Book>lambdaQuery()
                        .eq(Book::getIsbn,isbn)
                        .ne(
                                bookId != null,
                                Book::getBookId,
                                bookId
                        )
        );

        if(count > 0) {
            throw new BusinessException("该ISBN对应图书已存在");
        }
    }

    private BookDetailVO toVO(Book book){
        BookDetailVO bookDetailVO = new BookDetailVO();

        bookDetailVO.setBookId(book.getBookId());
        bookDetailVO.setIsbn(book.getIsbn());
        bookDetailVO.setTitle(book.getTitle());
        bookDetailVO.setAuthor(book.getAuthor());
        bookDetailVO.setPublisher(book.getPublisher());
        bookDetailVO.setPublishDate(book.getPublishDate());
        bookDetailVO.setCategoryId(book.getCategoryId());
        bookDetailVO.setPrice(book.getPrice());
        bookDetailVO.setDescription(book.getDescription());
        bookDetailVO.setCoverUrl(book.getCoverUrl());
        bookDetailVO.setTotalCopies(book.getTotalCopies());
        bookDetailVO.setAvailableCopies(book.getAvailableCopies());
        bookDetailVO.setLocation(book.getLocation());

        return bookDetailVO;
    }

    private BookListVO toListVO(Book book) {
        BookListVO bookListVO =  new BookListVO();

        bookListVO.setBookId(book.getBookId());
        bookListVO.setIsbn(book.getIsbn());
        bookListVO.setTitle(book.getTitle());
        bookListVO.setAuthor(book.getAuthor());
        bookListVO.setCategoryId(book.getCategoryId());
        bookListVO.setCoverUrl(book.getCoverUrl());
        bookListVO.setPrice(book.getPrice());
        bookListVO.setAvailableCopies(book.getAvailableCopies());

        return bookListVO;
    }
}
