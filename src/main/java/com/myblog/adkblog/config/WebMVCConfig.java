package com.myblog.adkblog.config;

import com.myblog.adkblog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    //跨域设置 可以实现前后端分离的端口号不同的情况
    static final String ORIGINS[]=new String[]{"GET","POST","DELETE","PUT"};
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOriginPatterns("*")
        .allowCredentials(true)
        .allowedMethods(ORIGINS)
        .maxAge(3600);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/articles/publish")
                .addPathPatterns("/comments/addcomment")
                .addPathPatterns("/tags/add")
                .addPathPatterns("/articles/myarticle")
                .addPathPatterns("/articles/updatemyarticle")
                .addPathPatterns("/articles/deletemyarticle/**")
                .addPathPatterns("/articles/switcharticlestate/**")
                .addPathPatterns("/login/logout")
                .addPathPatterns("/ws/**");
        //添加一个登录拦截器
    }

}
