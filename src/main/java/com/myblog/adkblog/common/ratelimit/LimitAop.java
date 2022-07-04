package com.myblog.adkblog.common.ratelimit;

import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.Result;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LimitAop {
    private static ConcurrentHashMap<String, ExpiringMap<String,Integer>> map=new ConcurrentHashMap<>();

    @Pointcut("@annotation(limit)")
    public void controllerAspect(Limit limit){

    }

    @Around("controllerAspect(limit)")
    public  Object doAround(ProceedingJoinPoint pjp,Limit limit) throws Throwable {
        // 获得request对象
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        // 获取Map value对象， 如果没有则返回默认值
        // //getOrDefault获取参数，获取不到则给默认值
        ExpiringMap<String, Integer> em = map.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
        Integer Count = em.getOrDefault(request.getRemoteAddr(), 0);
        // result的值就是被拦截方法的返回值

        if (Count >= limit.value()) {
            // 超过次数，不执行目标方法
            return Result.fail(ErrorCode.RATE_LIMIT.getCode(), ErrorCode.RATE_LIMIT.getMsg());
        } else if (Count == 0) {
            // 第一次请求时，设置有效时间
            em.put(request.getRemoteAddr(), Count + 1, ExpirationPolicy.CREATED, limit.time(), TimeUnit.MILLISECONDS);
        } else {
            em.put(request.getRemoteAddr(), Count + 1);
        }
        // 只要请求失败 那么就直接返回 不计入次数内(接口限制的是数据库的写入 并不限制其他的
        Object result = pjp.proceed();
        if (result instanceof Result) {
            Object data = ((Result) result).getData();
            if (data == null) {
                //建立于所有失败请求都不会设置data的前提下
                em.put(request.getRemoteAddr(), Count - 1);
                return result;
            }
        }
        map.put(request.getRequestURI(), em);
        return result;
    }
}
