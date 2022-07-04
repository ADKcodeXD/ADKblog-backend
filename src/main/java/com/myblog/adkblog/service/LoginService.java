package com.myblog.adkblog.service;

import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.vo.Params.LoginParams;
import com.myblog.adkblog.vo.Params.RegisterParams;
import com.myblog.adkblog.vo.Common.Result;

public interface LoginService {
    Result login(LoginParams loginParams);
    Result register(RegisterParams registerParams);
    User checkToken(String token);
    Result logout(String token);
}
