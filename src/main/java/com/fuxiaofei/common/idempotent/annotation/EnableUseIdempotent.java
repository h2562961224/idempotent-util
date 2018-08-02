package com.fuxiaofei.common.idempotent.annotation;

import com.fuxiaofei.common.idempotent.config.IdempotentAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author huxiaohui
 * @date 2018/8/1 09:45
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(IdempotentAutoConfig.class)
@Documented
public @interface EnableUseIdempotent {
}
