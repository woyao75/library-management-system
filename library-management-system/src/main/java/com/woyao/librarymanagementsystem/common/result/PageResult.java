package com.woyao.librarymanagementsystem.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private List<T> records;    // 当前页数据列表
    private Long total;         // 总记录数
    private Long current;    // 当前页码
    private Long size;       // 每页大小
    private Long pages;      // 总页数

    public static <T> PageResult<T> of(IPage<T> page){
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getPages()
        );
    }
}
