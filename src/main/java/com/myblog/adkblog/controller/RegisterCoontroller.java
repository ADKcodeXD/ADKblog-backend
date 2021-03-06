package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.ratelimit.Limit;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.vo.Params.RegisterParams;
import com.myblog.adkblog.vo.Common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
@Api(tags="注册Api")
public class RegisterCoontroller {
    @Autowired
    private LoginService loginService;

    @PostMapping
    @ApiOperation("注册用户Api")
    @Limit(time = 60000,value = 1) //同一ip 一分钟内只能注册一次
    public Result login(@RequestBody RegisterParams registerParams){
        return loginService.register(registerParams);
    }
}
