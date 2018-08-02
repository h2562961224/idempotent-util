package com.fuxiaofei.common.idempotent.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fuxiaofei.common.idempotent.config.IdempotentConfigProperties;
import com.fuxiaofei.common.idempotent.annotation.Idempotent;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * @author huxiaohui
 * @date 2018/8/1 11:44
 */
@Data
public class IdempotentConfig implements Serializable {
    private String nameSpace;
    private String prefix;
    private Long expireMilliSecond;
    private Class doingExecutorClass;

    private Integer fieldSize;
    private List<Field[]> fieldsList;
    private JavaType returnJavaType;

    public static IdempotentConfig from(String prefix, Idempotent idempotent, IdempotentConfigProperties idempotentConfigProperties, List<Field[]> fieldsList, Integer fieldSize){
        IdempotentConfig idempotentConfig = new IdempotentConfig();
        idempotentConfig.setPrefix(prefix);
        idempotentConfig.setNameSpace(idempotent.value());
        idempotentConfig.setDoingExecutorClass(idempotent.doingExecutorClass().isInterface() ? DefaultExceptionDoingExecutor.class : idempotent.doingExecutorClass());
        idempotentConfig.setExpireMilliSecond(Objects.nonNull(idempotent.expireMilliSecond()) ? idempotent.expireMilliSecond():idempotentConfigProperties.getExpireMilliSecond());
        idempotentConfig.setFieldsList(fieldsList);
        idempotentConfig.setFieldSize(fieldSize);
        return idempotentConfig;
    }
}
