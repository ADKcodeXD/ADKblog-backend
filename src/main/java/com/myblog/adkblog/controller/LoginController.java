package com.myblog.adkblog.controller;

import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.vo.Params.LoginParams;
import com.myblog.adkblog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
@Api(tags="登录Api")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping
    @ApiOperation("登录Api")
    public Result login(@RequestBody LoginParams loginParams){
        return loginService.login(loginParams);
    }
}
