package com.woyao.librarymanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.woyao.librarymanagementsystem.entity.Book;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    /**
     * 锁定图书
     */
    @Select("select * from book where book_id = #{id} and is_deleted = 0 for update")
    Book lockById(@Param("id") Integer id);

    /**
     * 调整库存
     */
    @Update("""
            update book set available_copies = available_copies - 1,
                            update_time = NOW()
            where book_id = #{id}
            and is_deleted = 0
            and available_copies > 0
            """)
    int decrease(@Param("id") int id);

    @Update("""
            update book set available_copies = available_copies + 1,
                            update_time = NOW()
            where book_id = #{id}
            and is_deleted = 0
            and available_copies < total_copies
            """)
    int increase(@Param("id") int id);

    @Update("""
            update book
            set total_copies = #{total}, available_copies = #{available}, update_time = NOW()
            where book_id = #{id} and is_deleted = 0
            """)
    int adjust(@Param("id") int id, @Param("total") int total, @Param("available") int available);
}
