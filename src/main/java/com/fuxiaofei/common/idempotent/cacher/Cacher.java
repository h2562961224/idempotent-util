package com.fuxiaofei.common.idempotent.cacher;

/**
 * @author huxiaohui
 * @date 2018/8/1 09:25
 */
public interface Cacher {
    void expire(String key, String value, Long milliSecond);

    String get(String key);
}
