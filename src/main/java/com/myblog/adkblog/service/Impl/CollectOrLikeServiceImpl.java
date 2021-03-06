package com.myblog.adkblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.ArticleMapper;
import com.myblog.adkblog.dao.mapper.ArticleUserCollectMapper;
import com.myblog.adkblog.dao.mapper.ArticleUserLikeMapper;
import com.myblog.adkblog.pojo.*;
import com.myblog.adkblog.service.CollectOrLikeService;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.service.TagService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Views.ArticleVo;
import com.myblog.adkblog.vo.Views.UserVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectOrLikeServiceImpl implements CollectOrLikeService {

    @Autowired
    private LoginService loginService;
    @Autowired
    private ArticleUserCollectMapper articleUserCollectMapper;
    @Autowired
    private ArticleUserLikeMapper articleUserLikeMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Override
    public Result<ListInfoVo<ArticleVo>> getMyCollect(PageParams pageParams, String token) {
        Page<ArticleUserCollect> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        ListInfoVo<ArticleVo> infoVo = new ListInfoVo<>();
        User user = loginService.checkToken(token);
        if(user==null){
            return  Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        LambdaQueryWrapper<ArticleUserCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleUserCollect::getUserId,user.getId());
        Page<ArticleUserCollect> articleUserCollectPage = articleUserCollectMapper.selectPage(page, queryWrapper);
        List<ArticleUserCollect> records = articleUserCollectPage.getRecords();
        ArrayList<Long> collectIds = new ArrayList<>();

        for (ArticleUserCollect record : records) {
            collectIds.add(record.getArticleId());
        }
        //?????????????????? ???????????????????????????
        if(records.size()==0) {
            infoVo.setLength((long) 0);
            return  Result.success(infoVo);
        }
        List<Article> articles = articleMapper.selectBatchIds(collectIds);
        //???????????????body ??????????????????null
        List<ArticleVo> articleVos = copyList(articles,records);
        infoVo.setResults(articleVos);
        infoVo.setLength(articleUserCollectPage.getTotal());

        return Result.success(infoVo);
    }

    @Override
    public Result<ListInfoVo<ArticleVo>> getMyLiked(PageParams pageParams, String token) {
        Page<ArticleUserLike> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        ListInfoVo<ArticleVo> infoVo = new ListInfoVo<>();
        User user = loginService.checkToken(token);
        if(user==null){
            return  Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        LambdaQueryWrapper<ArticleUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleUserLike::getUserId,user.getId());
        Page<ArticleUserLike> articleUserLikePage = articleUserLikeMapper.selectPage(page, queryWrapper);
        List<ArticleUserLike> records = articleUserLikePage.getRecords();
        ArrayList<Long> collectIds = new ArrayList<>();

        for (ArticleUserLike record : records) {
            collectIds.add(record.getArticleId());
        }
        //?????????????????? ???????????????????????????
        if(records.size()==0) {
            infoVo.setLength((long) 0);
            return  Result.success(infoVo);
        }
        List<Article> articles = articleMapper.selectBatchIds(collectIds);
        //???????????????body ??????????????????null
        List<ArticleVo> articleVos = copyList(articles,records);

        infoVo.setResults(articleVos);
        infoVo.setLength(articleUserLikePage.getTotal());

        return Result.success(infoVo);
    }

    @Override
    public Result deleteMyCollect(String articleId, String token) {
        User user = loginService.checkToken(token);
        if(user==null){
            return  Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        LambdaQueryWrapper<ArticleUserCollect> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleUserCollect::getArticleId,Long.valueOf(articleId));
        lambdaQueryWrapper.eq(ArticleUserCollect::getUserId,user.getId());
        articleUserCollectMapper.delete(lambdaQueryWrapper);
        return Result.success(null);
    }

    @Override
    public Result deleteMyLiked(String articleId, String token) {
        User user = loginService.checkToken(token);
        if(user==null){
            return  Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        LambdaQueryWrapper<ArticleUserLike> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleUserLike::getArticleId,Long.valueOf(articleId));
        lambdaQueryWrapper.eq(ArticleUserLike::getUserId,user.getId());
        articleUserLikeMapper.delete(lambdaQueryWrapper);
        return Result.success(null);
    }

    /**
     * ???????????????????????????????????? ?????????????????????????????????vo??????
     * @param article
     * @return
     */
    private ArticleVo copy(Article article){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        //????????????long?????????????????????string???????????????????????????
        String id=article.getId().toString();
        articleVo.setId(id);
        //???????????????????????????bean???????????????copy???????????????????????????????????????copy???
        //??????????????????long????????????????????????
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //????????????????????????????????????????????????????????????
        //???????????????????????? isTag???isAuthor ????????????????????????????????????
        Long articleid=article.getId();
        articleVo.setTags(tagService.findTagByArticleId(articleid));
        articleVo.setIsCollected(true);
        //???????????? ??????
        Long authorId = article.getAuthorId();
        UserVo userVo = userService.findUserVoById(authorId);
        articleVo.setAuthorVo(userVo);
        articleVo.setAuthor(userVo.getUsername());
        return  articleVo;
    }

    private <T extends ArticleRecord> List<ArticleVo> copyList(List<Article> list,List<T> records){
        ArrayList<ArticleVo> res = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            ArticleVo copy = copy(list.get(i));
            //?????? ??????????????????
            copy.setCreateDate(records.get(i).getCreateTime().toString());
            res.add(copy);
        }
        return res;
    }
}
