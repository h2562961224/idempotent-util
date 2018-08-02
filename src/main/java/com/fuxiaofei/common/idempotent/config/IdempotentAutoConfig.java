package com.fuxiaofei.common.idempotent.config;

import com.fuxiaofei.common.idempotent.cacher.Cacher;
import com.fuxiaofei.common.idempotent.cacher.DeFaultGuavaCacher;
import com.fuxiaofei.common.idempotent.core.DefaultExceptionDoingExecutor;
import com.fuxiaofei.common.idempotent.core.DefaultValueDoingExecutor;
import com.fuxiaofei.common.idempotent.core.DoingExecutor;
import com.fuxiaofei.common.idempotent.core.IdempotentMethodAop;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huxiaohui
 * @date 2018/8/1 09:43
 */
@Configuration
@EnableConfigurationProperties({IdempotentConfigProperties.class})
public class IdempotentAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Cacher resolveDefault(){
        Cacher cacher = new DeFaultGuavaCacher();
        return cacher;
    }

    @Bean
    public DoingExecutor resolveDoingExecutor(){
        return new DefaultExceptionDoingExecutor();
    }

    @Bean
    public DoingExecutor resolveDefaultValueDoingExecutor(){
        return new DefaultValueDoingExecutor();
    }

    @Bean
    public IdempotentMethodAop resolveIdempotentMethodAop(IdempotentConfigProperties idempotentConfigProperties, ApplicationContext applicationContext,Cacher cacher){
        return new IdempotentMethodAop(idempotentConfigProperties,applicationContext,cacher);
    }
}
