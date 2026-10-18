package com.woyao.librarymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.woyao.librarymanagementsystem.entity.BookCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {
}
