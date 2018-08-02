package com.fuxiaofei.common.idempotent.cacher;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.fuxiaofei.common.idempotent.util.StringUtil;
import lombok.Getter;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author huxiaohui
 * @date 2018/8/1 16:49
 */
public class DeFaultGuavaCacher implements Cacher {
    @Getter
    private LoadingCache<String,Optional<LoadingCache<String,String>>> cacher;

    private final static String NULL_VALUE = "null_value";

    @PostConstruct
    public void init() {
        this.cacher = CacheBuilder.newBuilder().maximumSize(10000L).build(new CacheLoader<String,Optional<LoadingCache<String,String>>>() {
            @Override
            public Optional<LoadingCache<String,String>> load(String s){
                return Optional.absent();
            }
        });
    }

    @Override
    public void expire(String key, String value, Long milliSecond) {
        String[] keyPart = key.split("_");
        String namespace = keyPart[0];
        String id = keyPart.length > 1 ? keyPart[1]:"0";
        Optional<LoadingCache<String,String>> optional = cacher.getUnchecked(namespace);
        if(!optional.isPresent()){
            LoadingCache<String,String> privateCacher = CacheBuilder.newBuilder().expireAfterWrite(milliSecond, TimeUnit.MILLISECONDS).maximumSize(10000L).build(new CacheLoader<String, String>() {
                @Override
                public String load(String s){
                    return NULL_VALUE;
                }
            });
            optional = Optional.of(privateCacher);
            cacher.put(namespace,optional);
        }
        optional.get().put(id,StringUtil.isEmpty(value) ? NULL_VALUE : value);
    }

    @Override
    public String get(String key) {
        String[] keyPart = key.split("_");
        String namespace = keyPart[0];
        String id = keyPart.length > 1 ? keyPart[1]:"0";
        Optional<LoadingCache<String,String>> optional = cacher.getUnchecked(namespace);
        if(!optional.isPresent()){
            return null;
        }
        String value = optional.get().getUnchecked(id);
        return NULL_VALUE.equals(value) ? null : value;
    }
}
