package com.woyao.librarymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("borrow_record")
public class BorrowRecord {

    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;

    private Integer userId;
    private Integer bookId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer status;
    private Integer renewCount;
    private Integer operatorId;

    @TableField(value = "create_at", fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
