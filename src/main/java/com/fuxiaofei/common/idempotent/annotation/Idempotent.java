package com.fuxiaofei.common.idempotent.annotation;

import com.fuxiaofei.common.idempotent.core.DoingExecutor;

import java.lang.annotation.*;

/**
 * @author huxiaohui
 * @date 2018/8/1 09:28
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /**
     * 作用域前缀
     * @return
     */
    String value();

    Class doingExecutorClass() default DoingExecutor.class;

    long expireMilliSecond() default 180000L;
}
