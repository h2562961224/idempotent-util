package com.fuxiaofei.common.idempotent.core;

import com.fuxiaofei.common.idempotent.util.BeanUtil;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author huxiaohui
 * @date 2018/8/1 21:20
 */
public class DefaultValueDoingExecutor implements DoingExecutor {

    @Override
    public Object processWhileDoing(ProceedingJoinPoint proceedingJoinPoint, IdempotentConfig idempotentConfig) {
        return BeanUtil.defaultValueForClass(idempotentConfig.getReturnJavaType().getRawClass());
    }

}
