package com.myblog.adkblog.service;

import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.vo.Params.LikeOrCollectParams;
import com.myblog.adkblog.vo.Params.UpdateUserParams;
import com.myblog.adkblog.vo.Result;
import com.myblog.adkblog.vo.UserVo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserService {
    User findUser(String username,String password);
    void saveUser(User user);
    User findUserByUsername(String username);
    Result findUserByToken(String token);
    User findUserById(Long id);
    Result findUserAllByToken(String token);
    UserVo findUserVoById(Long authorId);
    UserVo findUserVoByToken(String token);
    Result updateUserInfoByToken(String token, UpdateUserParams updateUserParams);
    Result likeArticle(LikeOrCollectParams likeParams,String token);
    Result collectArticle(LikeOrCollectParams likeParams, String token);
}
