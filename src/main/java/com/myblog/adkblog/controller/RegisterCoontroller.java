package com.myblog.adkblog.controller;

import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.vo.Params.LoginParams;
import com.myblog.adkblog.vo.Params.RegisterParams;
import com.myblog.adkblog.vo.Result;
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
    public Result login(@RequestBody RegisterParams registerParams){
        return loginService.register(registerParams);
    }
}
