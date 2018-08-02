package com.fuxiaofei.common.idempotent.util;

import java.util.Objects;

/**
 * @author huxiaohui
 * @date 2018/8/1 11:49
 */
public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    public static String firstNotEmpty(String... contents ){
        if(Objects.nonNull(contents) && contents.length > 0){
            for(String content:contents){
                if(StringUtil.isNotEmpty(content)){
                    return content;
                }
            }
            return contents[contents.length - 1];
        } else {
            throw new NullPointerException();
        }
    }
}
