package com.woyao.librarymanagementsystem.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.woyao.librarymanagementsystem.common.exception.BusinessException;
import com.woyao.librarymanagementsystem.entity.Book;
import com.woyao.librarymanagementsystem.entity.BorrowRecord;
import com.woyao.librarymanagementsystem.enums.BorrowStatus;
import com.woyao.librarymanagementsystem.mapper.BookMapper;
import com.woyao.librarymanagementsystem.mapper.BorrowRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowService {
    private static final int LOAN_DAY = 30;

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Transactional
    public int borrow(int userId, int bookId){
        // 1. 锁住图书行
        Book book = lockBook(bookId);

        if(book == null){
            throw new BusinessException(404,"图书不存在");
        }

        // 3. 不要在没有行锁时，先读取库存，再把计算结果直接写回
        int updatedRows = bookMapper.update(
                null,
                Wrappers.<Book>lambdaUpdate()
                        .eq(Book::getBookId,bookId)
                        .gt(Book::getAvailableCopies,0)
                        .setDecrBy(Book::getAvailableCopies,1)
                        .set(Book::getUpdateTime, LocalDateTime.now())
        );

        if(updatedRows != 1){
            throw new BusinessException(409, "库存不足");
        }

        // 4. 创建 借阅记录，一次 set 调用只设置一个字段
        LocalDateTime borrowTime = LocalDateTime.now();
        LocalDateTime dueTime = borrowTime.plusDays(LOAN_DAY);
        BorrowRecord record = new BorrowRecord();

        record.setUserId(userId);
        record.setBookId(bookId);
        record.setStatus(BorrowStatus.BORROWED.getCode());
        record.setBorrowDate(borrowTime);
        record.setDueDate(dueTime);
        record.setRenewCount(0);
        record.setOperatorId(userId);
        record.setIsDeleted(0);

        // 5. 保存记录，插入失败时，事务也会撤销第 3 步的库存修改
        int insertedRows = borrowRecordMapper.insert(record);

        if(insertedRows != 1){
            throw new BusinessException("借阅记录保存失败");
        }

        return record.getRecordId();
    }

    private Book lockBook(int bookId) {
        return bookMapper.selectOne(
                Wrappers.<Book>lambdaQuery()
                        .eq(Book::getBookId,bookId)
                        .last("for update")
        );
    }
}
