package com.myblog.adkblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.myblog.adkblog.dao.mapper.UserMapper;
import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.utils.JWTUtils;
import com.myblog.adkblog.vo.ErrorCode;
import com.myblog.adkblog.vo.Params.LoginParams;
import com.myblog.adkblog.vo.Params.RegisterParams;
import com.myblog.adkblog.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    private static final String slat="adkblog@@#!%$%#%$12";
    @Override
    public Result login(LoginParams loginParams) {
        String username=loginParams.getUsername();
        String password=loginParams.getPassword();
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        password=DigestUtils.md5Hex(password+slat);
        User user=userService.findUser(username,password);

        if(user==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        String token = JWTUtils.createToken(user.getId());
        return Result.success(token);
    }

    @Override
    public Result register(RegisterParams registerParams) {
        String username=registerParams.getUsername();
        String password=registerParams.getPassword();
        String nickname=registerParams.getNickname();
        if (StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        User user=userService.findUserByUsername(username);
        if(user!=null){
            return  Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        User newer=new User();
        newer.setAvatar("/default_avatar.png");
        newer.setUsername(registerParams.getUsername());
        newer.setPassword(DigestUtils.md5Hex(registerParams.getPassword()+slat));
        newer.setNickname(registerParams.getNickname());
        newer.setDate(System.currentTimeMillis());
        newer.setRole("GUEST");
        newer.setBanner("/default_banner.png");
        newer.setIntroduce("用一句话介绍自己吧~");
        newer.setGender(1);
        userService.saveUser(newer);
        //通过jwt加密用户类 并获取token
        String token = JWTUtils.createToken(newer.getId());

        return Result.success(token);
    }

    @Override
    public User checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        System.out.println(token);
        //使用jwtutils来进行token的检验，若不合法则返回空值
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map==null){
            return null;
        }
        //暂时不用redis  直接解析去mysql中获取
        User user = userMapper.selectById((Serializable) map.get("userId"));

        return user;
    }


}
