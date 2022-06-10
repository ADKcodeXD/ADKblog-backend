package com.myblog.adkblog.handler;

import com.alibaba.fastjson.JSON;
import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.utils.UserThreadLocal;
import com.myblog.adkblog.vo.ErrorCode;
import com.myblog.adkblog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 用于拦截资源
 * 继承HandlerInterceptor这个类
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;

    /**
     * 执行方法之前执行该方法 preHandle
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、判断请求的接口路径  是否为controller中的方法
        //2、判断token是否为空 为空则进行拦截
        //3、使用loginservice中的check进行token认证
        //4、写完拦截器后需要到webmvcconfig中配置拦截器，将该类加进去。
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token= request.getHeader("Authorization");


        if(StringUtils.isBlank(token)){
            Result result=Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        User user = loginService.checkToken(token);

        if(user==null){
            Result result=Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        UserThreadLocal.put(user);
        //然后我们就可以在controller层直接获取对象
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}