package com.myblog.adkblog.common.redis;

import com.alibaba.fastjson.JSON;
import com.myblog.adkblog.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class CacheAop {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(cache)")
    public void controllerCache(Cache cache) {
    }

    @Around("controllerCache(cache)")
    public Object around(ProceedingJoinPoint pjp, Cache cache) {
        try {
            Signature signature = pjp.getSignature();
            //类名
            String className = pjp.getTarget().getClass().getSimpleName();
            //调用的方法名
            String methodName = signature.getName();
            //从pjp中获取参数的类型 并事先预设好用一个数组存放
            Class[] parameterTypes = new Class[pjp.getArgs().length];
            //获取了参数的数量
            Object[] args = pjp.getArgs();
            //参数
            String params = "";
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    params += JSON.toJSONString(args[i]);
                    parameterTypes[i] = args[i].getClass();
                } else {
                    parameterTypes[i] = null;
                }
            }
            if (StringUtils.isNotEmpty(params)) {
                //加密 以防出现key过长以及字符转义获取不到的情况
                params = DigestUtils.md5Hex(params);
            }
            //rediskey的组成 当前的缓存名+“::”分割符号+当前类名+"::"分割符号+“方法名”+""::"+参数(md5加密了)
            //示例: article::ArticleController::getArticle::e255678%#!shaiufa77parmsshgasiu
            //也就是参数不同也会一并将结果存入redis缓存
            String redisKey = cache.name() + "::" + className + "::" + methodName + "::" + params;
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNotEmpty(redisValue)) {
                Result result = JSON.parseObject(redisValue, Result.class);
                return result;
            }
            //如果没有找到相应结果 就会从切点中获取返回的对应的结果
            Object proceed = pjp.proceed();
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(proceed), Duration.ofMillis(cache.expire()));
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Result.fail(999, "系统错误");
    }
}
