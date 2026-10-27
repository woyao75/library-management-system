package com.woyao.librarymanagementsystem.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.woyao.librarymanagementsystem.common.exception.BusinessException;
import com.woyao.librarymanagementsystem.common.result.PageResult;
import com.woyao.librarymanagementsystem.dto.response.BorrowVO;
import com.woyao.librarymanagementsystem.entity.Book;
import com.woyao.librarymanagementsystem.entity.BorrowRecord;
import com.woyao.librarymanagementsystem.enums.BorrowStatus;
import com.woyao.librarymanagementsystem.mapper.BookMapper;
import com.woyao.librarymanagementsystem.mapper.BorrowRecordMapper;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        // 2. 查询当前用户是否还有这本书未归还
        long activeCount = borrowRecordMapper.selectCount(
                Wrappers.<BorrowRecord>lambdaQuery()
                        .eq(BorrowRecord::getBookId,bookId)
                        .eq(BorrowRecord::getUserId,userId)
                        .eq(BorrowRecord::getStatus,BorrowStatus.BORROWED.getCode())
        );

        if(activeCount > 0){
            throw new BusinessException(409,"该书尚未归还");
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

    public BorrowVO detail(int userId, int recordId) {
        BorrowRecord record = borrowRecordMapper.selectOne(
                Wrappers.<BorrowRecord>lambdaQuery()
                        .eq(BorrowRecord::getRecordId,recordId)
                        .eq(BorrowRecord::getUserId,userId)
        );

        if(record == null){
            throw new BusinessException(404, "借阅记录不存在");
        }

        return toVO(record);
    }

    public PageResult<BorrowVO> pageMine(int userId, long current, long size){
        // 告诉数据库分页的规则
        Page<BorrowRecord> pageRequest = new Page<>(current, size);

        Page<BorrowRecord> recordPage = borrowRecordMapper.selectPage(
                pageRequest,
                Wrappers.<BorrowRecord>lambdaQuery()
                        .eq(BorrowRecord::getUserId,userId)
                        .orderByDesc(BorrowRecord::getRecordId)
        );

        List<BorrowVO> items = new ArrayList<>();

        for(BorrowRecord record : recordPage.getRecords()){
            BorrowVO item = toVO(record);
            items.add(item);
        }

        return new PageResult<>(
                items,
                recordPage.getTotal(),
                recordPage.getCurrent(),
                recordPage.getSize(),
                recordPage.getPages()
        );
    }

    @Transactional
    public void returnBook(int userId, int recordId){
        // 1. 读取记录，取得bookId
        BorrowRecord originalRecord = borrowRecordMapper.selectById(recordId);

        checkRecordOwner(originalRecord, userId);

        int bookId =  originalRecord.getBookId();

        // 2. 先锁图书，再锁借阅记录
        Book book = lockBook(bookId);

        BorrowRecord lockedRecord = borrowRecordMapper.selectOne(
                Wrappers.<BorrowRecord>lambdaQuery()
                        .eq(BorrowRecord::getRecordId,recordId)
                        .last("for update")
        );

        checkRecordOwner(lockedRecord, userId);

        // 3. 使用加锁后读取的状态，若重复归还直接结束
        if(lockedRecord.getStatus() == BorrowStatus.RETURNED.getCode()){
            return;
        }

        if(book == null){
            throw new BusinessException(409, "图书状态异常");

        }

        // 4. 更新条件同时检查记录归属和未归还状态
        LocalDateTime returnTime = LocalDateTime.now();

        int returnedRows = borrowRecordMapper.update(
                null,
                Wrappers.<BorrowRecord>lambdaUpdate()
                        .eq(BorrowRecord::getRecordId,recordId)
                        .eq(BorrowRecord::getUserId,userId)
                        .eq(BorrowRecord::getStatus,BorrowStatus.BORROWED.getCode())
                        .set(BorrowRecord::getStatus,BorrowStatus.RETURNED.getCode())
                        .set(BorrowRecord::getReturnDate,returnTime)
                        .set(BorrowRecord::getUpdateTime,returnTime)
        );

        if(returnedRows != 1){
            throw new BusinessException(409, "借阅状态发生变化");
        }

        // 5. 失败时，事务会撤销第 4 步
        int updateRows = bookMapper.update(
                null,
                Wrappers.<Book>lambdaUpdate()
                        .eq(Book::getBookId, bookId)
                        .lt(Book::getAvailableCopies,book.getTotalCopies())
                        .setIncrBy(Book::getAvailableCopies,1)
                        .set(Book::getUpdateTime,returnTime)
        );

        if(updateRows != 1){
            throw new BusinessException(409, "库存恢复失败");
        }
    }



    private void checkRecordOwner(BorrowRecord record, int userId) {
        if (record == null){
            throw new BusinessException(404, "借阅记录不存在");
        }
        if (record.getUserId() != userId){
            throw new BusinessException(404, "借阅记录不存在");
        }
    }

    private Book lockBook(int bookId) {
        return bookMapper.selectOne(
                Wrappers.<Book>lambdaQuery()
                        .eq(Book::getBookId,bookId)
                        .last("for update")
        );
    }

    private BorrowVO toVO(BorrowRecord record) {
        BorrowVO vo = new BorrowVO();

        vo.setRecordId(record.getRecordId());
        vo.setBookId(record.getBookId());
        vo.setStatus(record.getStatus());
        vo.setBorrowDate(record.getBorrowDate());
        vo.setDueDate(record.getDueDate());
        vo.setReturnDate(record.getReturnDate());

        return vo;
    }
}
