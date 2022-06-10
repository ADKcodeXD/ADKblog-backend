package com.myblog.adkblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.*;
import com.myblog.adkblog.dao.mapper.dos.ArticleTip;
import com.myblog.adkblog.dao.mapper.dos.GroupByTime;
import com.myblog.adkblog.pojo.*;
import com.myblog.adkblog.service.ArticleService;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.service.TagService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.utils.UserThreadLocal;
import com.myblog.adkblog.vo.*;
import com.myblog.adkblog.vo.Params.ArticleParams;
import com.myblog.adkblog.vo.Params.PageParams;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleUserCollectMapper articleUserCollectMapper;
    @Autowired
    private ArticleUserLikeMapper articleUserLikeMapper;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ArticleIndexMapper articleIndexMapper;
    @Data
    class Mylist {
        List<ArticleVo> articleVoList;
        Long length;
    }
    //这是首页获取文章列表以及列举文章列表所用
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        IPage<Article> articleIPage = articleMapper.listArticle(page, pageParams.getAuthorId(),pageParams.getTagIds(), pageParams.getYear(), pageParams.getMonth(),pageParams.getOrderRole(),pageParams.getKeyword());
        List<Article> records = articleIPage.getRecords();
        List<ArticleVo> articleVoList = copyList(records, true, true);
        return Result.success(articleVoList);
    }


    @Override
    public Result publish(ArticleParams articleParams) {
        //登录拦截器中如果请求带token  就可以记录当前用户
        User user = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(user.getId());
        article.setViewCounts(0);
        article.setSummary(articleParams.getSummary());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setArticleName(articleParams.getArticleName());
        if(articleParams.getBanner()!=""){
            article.setBanner(articleParams.getBanner());
        }else {
            article.setBanner("/static/banner.png");
        }
        //先插入文章 获取文章的id(数据库雪花算法自动生成)
        articleMapper.insert(article);
        //将tagid和articleid 加入到  article_tag关联表当中
        List<TagVo> tags = articleParams.getTags();

        if (tags!=null){
            for (TagVo tag : tags) {
                Long id = article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(id);
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody=new ArticleBody();

        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParams.getBody().getContent());
        articleBody.setHtml(articleParams.getBody().getContentHtml());

        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());

        return Result.success(map);
    }

    @Override
    public Result findArticleById(Long id,String token) {
        Article article=articleMapper.selectById(id);
        Article articleComments=new Article();
        articleComments.setViewCounts(article.getViewCounts()+1);
        ArticleVo articleVo = new ArticleVo();
        User user = loginService.checkToken(token);
        if(user!=null){
            articleVo = copy(article,true,true,true,user.getId());
        }else {
            articleVo = copy(article,true,true,true,null);
        }
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId,article.getId());
        /**
         * 使用线程池   进行阅读量的更新
         */
        articleMapper.update(articleComments,lambdaUpdateWrapper);

        return Result.success(articleVo);
    }

    @Override
    public Result getIndexBanner() {
        //新建分页
        PageParams pageParams =new PageParams();
        pageParams.setPage(1);
        pageParams.setPagesize(4);
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        //查找并赋值
        IPage<Article> articleIPage = articleIndexMapper.listArticle(page,pageParams.getAuthorId(),pageParams.getTagIds(), pageParams.getYear(), pageParams.getMonth(),pageParams.getOrderRole());
        List<Article> records = articleIPage.getRecords();
        ArrayList<ArticleVo> articleVos = new ArrayList<>();
        List<ArticleVo> list = copyList(records, true, true);
        return Result.success(list);
    }

    @Override
    public Result listArticleWithCount(PageParams pageParams) {
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        IPage<Article> articleIPage = articleMapper.listArticle(page,pageParams.getAuthorId(),pageParams.getTagIds(), pageParams.getYear(), pageParams.getMonth(),pageParams.getOrderRole(),pageParams.getKeyword());
        long total = articleIPage.getTotal();
        List<Article> records = articleIPage.getRecords();
        List<ArticleVo> articleVoList = copyList(records, true, true);
        //使用这个mapper查询表中的数据
        Mylist mylist = new Mylist();
        mylist.setArticleVoList(articleVoList);
        mylist.setLength(total);
        return Result.success(mylist);
    }

    @Override
    public void updateViewCountsById(Long id) {
        Article article = articleMapper.selectById(id);
        Article newArticle = new Article();
        newArticle.setCommentCounts(article.getCommentCounts()+1);
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,id);
        articleMapper.update(newArticle,updateWrapper);
    }

    @Override
    public Result getGroupByTime() {
        List<GroupByTime> groupByTime = articleMapper.getGroupByTime();
        return Result.success(groupByTime);
    }

    @Override
    public Result getIndexArticle(PageParams pageParams) {
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        IPage<Article> articleIPage = articleIndexMapper.listArticle(page, pageParams.getAuthorId(),pageParams.getTagIds(), pageParams.getYear(), pageParams.getMonth(),pageParams.getOrderRole());
        List<Article> records = articleIPage.getRecords();
        List<ArticleVo> articleVoList = copyList(records, true, true);
        return Result.success(articleVoList);
    }

    @Override
    public Result getSearchTip(String keyword) {
        Page<ArticleTip> page=new Page<>(1,10);
        IPage<ArticleTip> articleIPage = articleMapper.articleTip(page,keyword);
        List<ArticleTip> records = articleIPage.getRecords();
        return Result.success(records);
    }


    /**
     * 做转移，
     * 将数据库得来的records转换为渲染到页面上的ArticleAo
     * @param records
     * @return
     */
    //重载 需要作者和标签 以及封面
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,null));
        }
        return articleVoList;
    }
    //需要所有信息
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,Long userId) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,isBody,userId));
        }
        return articleVoList;
    }

    /**
     * 传入一个article对象 转换成articleAo
     * @param article
     * @return
     */
    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,Long userId){

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
        if (isTag){
            Long articleid=article.getId();
            articleVo.setTags(tagService.findTagByArticleId(articleid));
        }
        if (isAuthor){
            Long authorId = article.getAuthorId();
            UserVo userVo = userService.findUserVoById(authorId);
            articleVo.setAuthorVo(userVo);
            articleVo.setAuthor(userVo.getUsername());
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (userId!=null){
            //查找当前登录用户 是否有收藏或者点赞的记
            LambdaQueryWrapper<ArticleUserCollect> userCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<ArticleUserLike> userLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //创建两个wrapper
            userCollectLambdaQueryWrapper.eq(ArticleUserCollect::getArticleId,article.getId());
            userLikeLambdaQueryWrapper.eq(ArticleUserLike::getArticleId,article.getId());
            userCollectLambdaQueryWrapper.eq(ArticleUserCollect::getUserId,userId);
            userLikeLambdaQueryWrapper.eq(ArticleUserLike::getUserId,userId);
            //获取到结果
            ArticleUserCollect articleUserCollect = articleUserCollectMapper.selectOne(userCollectLambdaQueryWrapper);
            ArticleUserLike articleUserLike = articleUserLikeMapper.selectOne(userLikeLambdaQueryWrapper);
            if(articleUserLike!=null){
                articleVo.setIsLiked(true);
            }else {
                articleVo.setIsLiked(false);
            }

            if(articleUserCollect!=null){
                articleVo.setIsCollected(true);
            }else {
                articleVo.setIsCollected(false);
            }
        }
        return articleVo;
    }

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        articleBodyVo.setHtml(articleBody.getHtml());
        return articleBodyVo;
    }
}
