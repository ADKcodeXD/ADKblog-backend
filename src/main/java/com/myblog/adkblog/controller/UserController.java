package com.myblog.adkblog.controller;

import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.vo.Params.LikeOrCollectParams;
import com.myblog.adkblog.vo.Params.UpdateUserParams;
import com.myblog.adkblog.vo.Result;
import com.myblog.adkblog.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@Api(tags = "用户信息相关Api")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("currentUser")
    @ApiOperation("根据Token获取当前用户信息")
    public Result currentUser(@RequestHeader("Authorization")String token){
        return userService.findUserByToken(token);
    }

    @GetMapping("user")
    @ApiOperation("获取用户ById")
    public Result findUserById(@RequestParam Long userId){
        User user = userService.findUserById(userId);
        UserVo userVo =new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return Result.success(userVo);
    }

    @GetMapping("currentUserAll")
    @ApiOperation("获取当前用户所有信息")
    public Result findUserById(@RequestHeader("Authorization")String token){
        return userService.findUserAllByToken(token);
    }

    @PostMapping("updateUserInfo")
    @ApiOperation(value = "更新用户信息Api" ,notes = "更新用户信息Api")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "认证请求头",required = true,paramType = "String")
    })
    public Result updateUserInfoByToken(@RequestHeader("Authorization")String token,@RequestBody UpdateUserParams updateUserParams){
        return userService.updateUserInfoByToken(token,updateUserParams);
    }

    @PostMapping("like")
    @ApiOperation("点赞文章Api")
    public Result likeArticle(@RequestBody LikeOrCollectParams likeParams, @RequestHeader("Authorization")String token){
        return userService.likeArticle(likeParams,token);
    }
    @PostMapping("collect")
    @ApiOperation("收藏文章Api")
    public Result collectArticle(@RequestBody LikeOrCollectParams likeParams, @RequestHeader("Authorization")String token){
        return userService.collectArticle(likeParams,token);
    }

//    @PostMapping("collect")
//    public Result likeArticle(@RequestBody LikeOrCollectParams likeParams, @RequestHeader("Authorization")String token){
//        return userService.likeArticle(likeParams,token);
//    }
}
