package com.myblog.adkblog.common.ratelimit;

import com.myblog.adkblog.vo.ErrorCode;
import com.myblog.adkblog.vo.Result;
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
        ExpiringMap<String, Integer> em= map.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
        Integer Count = em.getOrDefault(request.getRemoteAddr(), 0);
        if (Count >= limit.value()) {
            // 超过次数，不执行目标方法
            return Result.fail(ErrorCode.RATE_LIMIT.getCode(),ErrorCode.RATE_LIMIT.getMsg());
        } else if (Count == 0){
            // 第一次请求时，设置有效时间
            em.put(request.getRemoteAddr(), Count + 1, ExpirationPolicy.CREATED, limit.time(), TimeUnit.MILLISECONDS);
        } else {
            em.put(request.getRemoteAddr(), Count + 1);
        }
        map.put(request.getRequestURI(), em);
        // result的值就是被拦截方法的返回值
        Object result = pjp.proceed();
        return result;
    }
}
