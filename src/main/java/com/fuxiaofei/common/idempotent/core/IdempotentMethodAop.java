package com.fuxiaofei.common.idempotent.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fuxiaofei.common.idempotent.IdempotentException;
import com.fuxiaofei.common.idempotent.config.IdempotentConfigProperties;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fuxiaofei.common.idempotent.annotation.Idempotent;
import com.fuxiaofei.common.idempotent.annotation.IdempotentParam;
import com.fuxiaofei.common.idempotent.cacher.Cacher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author huxiaohui
 * @date 2018/8/1 10:17
 */
@Aspect
@Order(0)
@SuppressWarnings("unused")
@Slf4j
public class IdempotentMethodAop  {
    private IdempotentConfigProperties idempotentConfigProperties;

    private ApplicationContext applicationContext;

    private Cacher cacher;

    public IdempotentMethodAop(IdempotentConfigProperties idempotentConfigProperties,ApplicationContext applicationContext,Cacher cacher){
        this.idempotentConfigProperties = idempotentConfigProperties;
        this.applicationContext = applicationContext;
        this.cacher = cacher;
    }

    private final static String DOING_FLAG = "doing_flag";

    private Map<String,IdempotentConfig> idempotentConfigMap = Maps.newConcurrentMap();

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @Pointcut(value = "@annotation(idempotent) ")
    public void apiPointcut(Idempotent idempotent) {
    }

    @Around("apiPointcut(idempotent)")
    public Object doIdempotent(ProceedingJoinPoint proceedingJoinPoint, Idempotent idempotent) throws Throwable {
        Object result = null;
        IdempotentConfig idempotentConfig = getAndSet(proceedingJoinPoint,idempotent);
        Object[] params = proceedingJoinPoint.getArgs();
        String key = getKey(idempotentConfig,params);

        String value = cacher.get(key);

        if(Objects.isNull(value) || value.length() == 0){
            cacher.expire(key,DOING_FLAG,idempotentConfig.getExpireMilliSecond());
            result = proceedingJoinPoint.proceed();
            cacher.expire(key,jsonMapper.writeValueAsString(result),idempotentConfig.getExpireMilliSecond());
        } else if(DOING_FLAG.equals(value)){
            DoingExecutor doingExecutor = getDogingExecutor(idempotentConfig);
            result = doingExecutor.processWhileDoing(proceedingJoinPoint,idempotentConfig);
        } else {
            result = jsonMapper.readValue(value,idempotentConfig.getReturnJavaType());
        }

        return result;
    }

    private String getKey(IdempotentConfig idempotentConfig,Object[] params){
        List<String> fieldValue = Lists.newArrayListWithCapacity(idempotentConfig.getFieldSize());
        List<Field[]> fieldsList = idempotentConfig.getFieldsList();
        IntStream.range(0,fieldsList.size()).forEach(index->{
            Field[] fields = fieldsList.get(index);
            Object param = params[index];
            if(Objects.isNull(fields)){
                return;
            }
            if(fields.length == 0){
                fieldValue.add(param.toString());
            } else {
                try {
                    for(Field field:fields){
                        fieldValue.add(field.get(param).toString());
                    }
                } catch (IllegalAccessException e){
                    fieldValue.add(param.toString());
                }
            }
        });
        return idempotentConfig.getPrefix()+ "_" + String.join("-",fieldValue);
    }

    private IdempotentConfig getAndSet(ProceedingJoinPoint proceedingJoinPoint, Idempotent idempotent){
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        String key = idempotent.value()+ "-" + method.getName();
        IdempotentConfig idempotentConfig = idempotentConfigMap.get(key);
        if(Objects.isNull(idempotentConfig)){
            Parameter[] parameters = method.getParameters();
            List<Field[]> fieldsList = Lists.newArrayListWithCapacity(parameters.length);
            Integer fieldSize = 0;
            for(Parameter parameter:parameters){
                IdempotentParam idempotentParam = parameter.getAnnotation(IdempotentParam.class);
                if(Objects.isNull(idempotentParam)){
                    fieldsList.add(null);
                } else {
                    Field[] fields = getFieldArray(parameter.getType(),idempotentParam.fields());
                    fieldsList.add(fields);
                    fieldSize += (fields.length == 0 ? 1:fields.length);
                }
            }
            idempotentConfig = IdempotentConfig.from(key,idempotent,idempotentConfigProperties,fieldsList,fieldSize);
            Type type = method.getGenericReturnType();
            JavaType javaType = jsonMapper.getTypeFactory().constructType(type);
            idempotentConfig.setReturnJavaType(javaType);
            idempotentConfigMap.put(key,idempotentConfig);
        }

        return idempotentConfig;
    }

    Field[] getFieldArray(Class paramClass,String[] fieldNames){
        Field[] fields = new Field[fieldNames.length];
        try{
            for(int i = 0;i < fieldNames.length;i ++){
                Field field = paramClass.getDeclaredField(fieldNames[i]);
                if(!field.isAccessible()){
                    field.setAccessible(Boolean.TRUE);
                }
                fields[i] = field;
            }
        } catch (NoSuchFieldException e){
            log.error("no such field , cause by {}", Throwables.getStackTraceAsString(e));
            throw new IdempotentException("no such field",e);
        }
        return fields;
    }

    private DoingExecutor getDogingExecutor(IdempotentConfig idempotentConfig){
        if(!DoingExecutor.class.isAssignableFrom(idempotentConfig.getDoingExecutorClass())){
            throw new IdempotentException("extends.not.from.doing.executor");
        }
        return (DoingExecutor)this.applicationContext.getBean(idempotentConfig.getDoingExecutorClass());
    }

    static {
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jsonMapper.registerModule(new GuavaModule());
    }
}
