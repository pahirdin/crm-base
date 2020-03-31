package com.asiainfo.crm.base.svc.framework.aop;

import com.asiainfo.bits.core.threadlocal.RequestTimeHolder;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 实现公共字段自动填充，各业务模块自由定义。
 * <p>
 * 注意：
 * <pre>
 * 1. 只针对非主键的字段, 且该字段注解了 fill 并且字段名和字段属性能匹配到，才会进行填充。
 * 2. 默认有值不覆盖。
 * </pre>
 *
 * @author Steven.zhou
 * @date 2020-03-06
 */
@Slf4j
@Component
public class BaseAutoSetMetaObjectAdvice implements MetaObjectHandler {

    /**
     * 记录创建时间
     */
    private static final String CREATE_DATE = "createDate";

    /**
     * 记录更新时间
     */
    private static final String DONE_DATE = "doneDate";

    /**
     * 记录创建工号
     */
    private static final String CREATE_OP_ID = "createOpId";

    /**
     * 记录更新工号
     */
    private static final String OP_ID = "opId";

    /**
     * 在新增记录时自动设置。
     *
     * @param metaObject 源对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime requestTime = getRequestTime();
        this.strictInsertFill(metaObject, CREATE_DATE, LocalDateTime.class, requestTime);
        this.strictInsertFill(metaObject, DONE_DATE, LocalDateTime.class, requestTime);
    }

    /**
     * 在修改记录时自动设置
     *
     * @param metaObject 源对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime requestTime = getRequestTime();
        this.strictInsertFill(metaObject, DONE_DATE, LocalDateTime.class, requestTime);
    }

    private LocalDateTime getRequestTime() {
        LocalDateTime requestTime = RequestTimeHolder.getRequestTime();
        if (null == requestTime) {
            requestTime = LocalDateTime.now();
        }
        return requestTime;
    }
}