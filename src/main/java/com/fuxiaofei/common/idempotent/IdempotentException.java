package com.fuxiaofei.common.idempotent;

/**
 * @author huxiaohui
 * @date 2018/8/1 14:31
 */
public class IdempotentException  extends RuntimeException  {
    public IdempotentException() {
        super();
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

}
