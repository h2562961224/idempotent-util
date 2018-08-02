package com.fuxiaofei.common.idempotent.core;

import com.fuxiaofei.common.idempotent.IdempotentException;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author huxiaohui
 * @date 2018/8/1 16:25
 */
public class DefaultExceptionDoingExecutor implements DoingExecutor {
    @Override
    public Object processWhileDoing(ProceedingJoinPoint proceedingJoinPoint, IdempotentConfig idempotentConfig) {
        throw new IdempotentException("another thread is processing this method");
    }
}
