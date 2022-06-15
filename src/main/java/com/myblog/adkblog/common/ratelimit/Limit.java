package com.myblog.adkblog.common.ratelimit;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
        // 默认一个小时
        long time() default 3000;
        //默认只能请求一次
        int value() default 1;
}
