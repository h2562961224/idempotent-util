package com.fuxiaofei.common.idempotent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author huxiaohui
 * @date 2018/8/1 09:39
 */
@Data
@ConfigurationProperties(prefix = IdempotentConfigProperties.IDEMPOTENT_PREFIX)
public class IdempotentConfigProperties {
    public static final String IDEMPOTENT_PREFIX = "idempotent";

    private String cacheType;
    private Long expireMilliSecond;
}
