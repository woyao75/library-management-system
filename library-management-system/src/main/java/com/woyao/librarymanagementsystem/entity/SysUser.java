package com.woyao.librarymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class SysUser {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String userName;
    private String password;
    private Integer role;
    private String phone;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String email;

    private Integer status;

    @TableField(value = "create_at", fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
