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
import com.myblog.adkblog.vo.*;
import com.myblog.adkblog.vo.Params.PageParams;
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
        //这里获取到了 所有的收藏的时间点
        if(records.size()==0) {
            infoVo.setLength((long) 0);
            return  Result.success(infoVo);
        }
        List<Article> articles = articleMapper.selectBatchIds(collectIds);
        //统一不需要body 所以都设置为null
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
        //这里获取到了 所有的收藏的时间点
        if(records.size()==0) {
            infoVo.setLength((long) 0);
            return  Result.success(infoVo);
        }
        List<Article> articles = articleMapper.selectBatchIds(collectIds);
        //统一不需要body 所以都设置为null
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
     * 由于不需要很多多余的信息 所以直接返回一个简单的vo即可
     * @param article
     * @return
     */
    private ArticleVo copy(Article article){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        //将查到的long型的数据转换为string赋值给前端展示对象
        String id=article.getId().toString();
        articleVo.setId(id);
        //该工具类可以将不同bean相同的属性copy过去，但是类型不同是不可以copy的

        //手动将日期的long类型转换为字符串
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        //由于并不是所有的接口都需要作者名和标签名
        //因此设置两个参数 isTag和isAuthor 来判断是否设置这两个参数
        Long articleid=article.getId();
        articleVo.setTags(tagService.findTagByArticleId(articleid));

        articleVo.setIsCollected(true);
        //获取作者 信息
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
            //另外 把时间给算上
            copy.setCreateDate(records.get(i).getCreateTime().toString());
            res.add(copy);
        }
        return res;
    }
}
