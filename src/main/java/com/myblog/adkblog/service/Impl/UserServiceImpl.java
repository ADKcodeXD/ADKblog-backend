package com.myblog.adkblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.myblog.adkblog.dao.mapper.ArticleMapper;
import com.myblog.adkblog.dao.mapper.ArticleUserCollectMapper;
import com.myblog.adkblog.dao.mapper.ArticleUserLikeMapper;
import com.myblog.adkblog.dao.mapper.UserMapper;
import com.myblog.adkblog.pojo.Article;
import com.myblog.adkblog.pojo.ArticleUserCollect;
import com.myblog.adkblog.pojo.ArticleUserLike;
import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Params.LikeOrCollectParams;
import com.myblog.adkblog.vo.Params.UpdateUserParams;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Views.UserAllVo;
import com.myblog.adkblog.vo.Views.UserVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ArticleUserLikeMapper articleUserLikeMapper;
    @Autowired
    private ArticleUserCollectMapper articleUserCollectMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public User findUser(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        queryWrapper.eq(User::getPassword,password);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public User findUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1.token合法性的校验
         * 2.redis是否存在
         * 3.成功则返回LoginUserVo
         */

        User user = loginService.checkToken(token);

        if (user==null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        //如果为空则直接返回失败的结果
        UserVo userVo =new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setId(user.getId().toString());
        return Result.success(userVo);
    }

    public Result findUserAllByToken(String token){
        User user = loginService.checkToken(token);
        if (user==null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        UserAllVo userAllVo=new UserAllVo();
        BeanUtils.copyProperties(user,userAllVo);
        userAllVo.setId(user.getId().toString());
        userAllVo.setDate(new DateTime(user.getDate()).toString("yyyy-MM-dd"));
        return Result.success(userAllVo);
    }

    @Override
    public User findUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserVo findUserVoById(Long id) {
        User user = userMapper.selectById(id);
        UserVo userVo=new UserVo();
        if(user==null){
            userVo.setUsername("已注销用户");
            userVo.setNickname("已注销用户");
            userVo.setIntroduce("该用户已注销");
            userVo.setAvatar("/default_avatar.png");
            userVo.setUsername("已注销用户");
        }else{
            BeanUtils.copyProperties(user,userVo);
            userVo.setId(user.getId().toString());
        }
        return userVo;
    }

    @Override
    public UserVo findUserVoByToken(String token) {
        User user = loginService.checkToken(token);
        if (user==null){
            return null;
        }
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setId(user.getId().toString());
        return userVo;
    }

    @Override
    public Result updateUserInfoByToken(String token, UpdateUserParams updateUserParams) {
        User user = loginService.checkToken(token);
        if(user==null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        User change = new User();
        //判断空值
        if(updateUserParams.getNickname()!=null){
            change.setNickname(updateUserParams.getNickname());
        }
        if(!updateUserParams.getAvatar().equals("")&& !updateUserParams.getAvatar().equals(user.getAvatar())){
            change.setAvatar(updateUserParams.getAvatar());
        }
        if(!updateUserParams.getBanner().equals("")&&!updateUserParams.getBanner().equals(user.getBanner())){
            change.setBanner(updateUserParams.getBanner());
        }
        if(updateUserParams.getEmail()!=null&&!updateUserParams.getEmail().equals(user.getEmail())){
            change.setEmail(updateUserParams.getEmail());
        }
        if(updateUserParams.getGender()!=null){
            Integer gender = Integer.valueOf(updateUserParams.getGender());
            change.setGender(gender);
        }
        if(updateUserParams.getIntroduce()!=null){
            change.setIntroduce(updateUserParams.getIntroduce());
        }
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId,user.getId());
        if(change!=null)
            userMapper.update(change,userLambdaUpdateWrapper);
        //更新缓存
        User user1 = userMapper.selectById(user.getId());
        redisTemplate.opsForValue().set("TOKEN_"
                + token, JSON.toJSONString(user1), 5, TimeUnit.DAYS);
        return Result.success(null);
    }

    @Override
    public Result likeArticle(LikeOrCollectParams likeParams, String  token) {
        User user = loginService.checkToken(token);
        if(user==null){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        Long id=user.getId();
        String articleId = likeParams.getArticleId();
        Article article = articleMapper.selectById(articleId);
        Article counts = new Article();
        if(likeParams.getFlag()){
            ArticleUserLike articleUserLike = new ArticleUserLike();
            articleUserLike.setUserId(id);
            articleUserLike.setArticleId(Long.valueOf(articleId));
            articleUserLike.setCreateTime(System.currentTimeMillis());
            articleUserLikeMapper.insert(articleUserLike);
            //操作article 让其收藏数+1;
            counts.setLikeCounts(article.getLikeCounts()+1);
            LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            articleLambdaUpdateWrapper.eq(Article::getId,Long.valueOf(articleId));
            articleMapper.update(counts,articleLambdaUpdateWrapper);
        }else {
            LambdaUpdateWrapper<ArticleUserLike> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(ArticleUserLike::getArticleId,Long.valueOf(articleId));
            lambdaUpdateWrapper.eq(ArticleUserLike::getUserId,id);
            articleUserLikeMapper.delete(lambdaUpdateWrapper);
            //操作article 让其点赞数-1;
            counts.setLikeCounts((article.getLikeCounts() - 1));
            LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            articleLambdaUpdateWrapper.eq(Article::getId,Long.valueOf(articleId));
            articleMapper.update(counts,articleLambdaUpdateWrapper);
        }

        return Result.success(null);
    }

    @Override
    public Result collectArticle(LikeOrCollectParams likeParams, String token) {
        User user = loginService.checkToken(token);
        if(user==null){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        Long id=user.getId();
        String articleId = likeParams.getArticleId();
        Article article = articleMapper.selectById(articleId);
        Article counts = new Article();

        if(likeParams.getFlag()){
            ArticleUserCollect articleUserCollect = new ArticleUserCollect();
            articleUserCollect.setUserId(id);
            articleUserCollect.setArticleId(Long.valueOf(articleId));
            articleUserCollect.setCreateTime(System.currentTimeMillis());
            articleUserCollectMapper.insert(articleUserCollect);
            //操作article 让其收藏数+1;
            counts.setCollectCounts(article.getCollectCounts()+1);
            LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            articleLambdaUpdateWrapper.eq(Article::getId,Long.valueOf(articleId));
            articleMapper.update(counts,articleLambdaUpdateWrapper);
        }else {
            LambdaUpdateWrapper<ArticleUserCollect> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(ArticleUserCollect::getArticleId,Long.valueOf(articleId));
            lambdaUpdateWrapper.eq(ArticleUserCollect::getUserId,id);
            articleUserCollectMapper.delete(lambdaUpdateWrapper);
            //操作article 让其收藏数-1;
            counts.setCollectCounts((article.getCollectCounts() - 1));
            LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            articleLambdaUpdateWrapper.eq(Article::getId,Long.valueOf(articleId));
            articleMapper.update(counts,articleLambdaUpdateWrapper);
        }

        return Result.success(null);
    }

}
