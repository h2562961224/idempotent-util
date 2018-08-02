package com.fuxiaofei.common.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author huxiaohui
 * @date 2018/8/1 14:14
 */
public interface DoingExecutor {
    Object processWhileDoing(ProceedingJoinPoint proceedingJoinPoint,IdempotentConfig idempotentConfig);
}
