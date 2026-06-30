package com.xzcit.duanfengheng.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);

        Long tempId = BaseContext.getCurrentId();
        // 定义final变量存储最终值，lambda可正常引用
        final Long userId = tempId == null ? 1L : tempId;

        this.strictInsertFill(metaObject, "createUser", () -> userId, Long.class);
        this.strictInsertFill(metaObject, "updateUser", () -> userId, Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);

        Long tempId = BaseContext.getCurrentId();
        final Long userId = tempId == null ? 1L : tempId;

        this.strictUpdateFill(metaObject, "updateUser", () -> userId, Long.class);
    }
}