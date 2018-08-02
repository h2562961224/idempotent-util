package com.fuxiaofei.common.idempotent.annotation;

import java.lang.annotation.*;

/**
 * @author huxiaohui
 * @date 2018/8/1 09:28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentParam {
    String[] fields() default {};
}
