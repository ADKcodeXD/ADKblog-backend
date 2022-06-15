package com.myblog.adkblog.common.redis;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    //默认时间 一小时内缓存有用
    long expire() default 1000 * 60 * 60;

    String name() default "defaultcache";
}
