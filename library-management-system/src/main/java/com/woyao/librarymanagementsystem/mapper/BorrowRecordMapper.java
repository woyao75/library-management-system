package com.woyao.librarymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.woyao.librarymanagementsystem.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
}
