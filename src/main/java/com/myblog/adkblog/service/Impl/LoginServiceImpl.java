package com.myblog.adkblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.myblog.adkblog.dao.mapper.UserMapper;
import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.utils.JWTUtils;
import com.myblog.adkblog.utils.UserThreadLocal;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Params.LoginParams;
import com.myblog.adkblog.vo.Params.RegisterParams;
import com.myblog.adkblog.vo.Common.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String slat = "adkblog@@#!%$%#%$12";

    @Override
    public Result login(LoginParams loginParams) {
        String username = loginParams.getUsername();
        String password = loginParams.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        password = DigestUtils.md5Hex(password + slat);
        User user = userService.findUser(username, password);
        if (user == null) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token = JWTUtils.createToken(user.getId());
        if (!StringUtils.isBlank(redisTemplate.opsForValue().get("TOKEN_" + token))) {
            return Result.fail(ErrorCode.IS_LOGINING.getCode(), ErrorCode.IS_LOGINING.getMsg());
        }
        //redis 中默认设置一天过期
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(user), 5, TimeUnit.DAYS);

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

        //redis 中默认设置一天过期
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(newer), 1, TimeUnit.DAYS);

        return Result.success(token);
    }

    @Override
    public User checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        //使用jwtutils来进行token的检验，若不合法则返回空值
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map == null) {
            return null;
        }
        //去redis 取出用户的信息缓存
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)) {
            return null;
        }
        //用阿里巴巴的fastjson将字符串解析成对象
        User user = JSON.parseObject(userJson, User.class);
        //update in 2022.6.15 引入redis
        //暂时不用redis  直接解析去mysql中获取
        //User user = userMapper.selectById((Serializable) map.get("userId"));
        return user;
    }

    @Override
    public Result logout(String token) {
        User user = UserThreadLocal.get();
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }
}
