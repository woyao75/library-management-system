package com.woyao.librarymanagementsystem.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充器配置
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject){
        LocalDateTime now = LocalDateTime.now();

        strictInsertFill(
                metaObject,
                "createAt",
                LocalDateTime.class,
                now
        );

        strictInsertFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                now
        );
    }

    @Override
    public void updateFill(MetaObject metaObject){
        strictUpdateFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
    }
}
