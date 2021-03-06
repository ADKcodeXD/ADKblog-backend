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
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Params.ArticleParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Views.ArticleBodyVo;
import com.myblog.adkblog.vo.Views.ArticleVo;
import com.myblog.adkblog.vo.Views.TagVo;
import com.myblog.adkblog.vo.Views.UserVo;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
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
    private ArticleIndexViewMapper articleIndexViewMapper;
    @Value("${sitemapPath}")
    private String sitemapPath;
    @Value("${baseUrl}")
    private String baseUrl;
    @Override
    public Result publish(ArticleParams articleParams) {
        //?????????????????????????????????token  ???????????????????????????
        User user = UserThreadLocal.get();
        Article article = new Article();
        article.setAuthorId(user.getId());
        article.setViewCounts(0);
        article.setSummary(articleParams.getSummary());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setArticleName(articleParams.getArticleName());
        article.setPannel(articleParams.getPannel());
        if(articleParams.getBanner()!=""){
            article.setBanner(articleParams.getBanner());
        }else {
            article.setBanner("/default_banner.png");
        }
        //??????????????? ???????????????id(?????????????????????????????????)
        articleMapper.insert(article);
        //???tagid???articleid ?????????  article_tag???????????????
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
        //??????sitemap
        try {
            //??????????????????????????????????????????????????????????????????????????????
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter (new FileOutputStream (sitemapPath,true),"UTF-8"));
            String site=baseUrl+"article/"+article.getId()+"\n";
            writer.write(site);
            writer.flush();
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(map);
    }
    @Override
    public Result findArticleById(Long id,String token) {
        Article article=articleMapper.selectById(id);
        User user = loginService.checkToken(token);
        if(article.getIsPrivate()==1 || article.getEnable()==0){
            if(user!=null && !user.getId().equals(article.getAuthorId())){
                //????????????????????????????????????????????????????????? ???????????????????????????????????????
                return Result.fail(ErrorCode.NO_PERMISSION.getCode(),ErrorCode.NO_PERMISSION.getMsg());
            }
        }
        Article articleComments=new Article();
        articleComments.setViewCounts(article.getViewCounts()+1);

        ArticleVo articleVo = new ArticleVo();

        if(user!=null){
            articleVo = copy(article,true,true,true,user.getId());
        }else {
            articleVo = copy(article,true,true,true,null);
        }

        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId,article.getId());
        articleMapper.update(articleComments,lambdaUpdateWrapper);

        return Result.success(articleVo);
    }
    @Override
    public Result getIndexBanner() {
        //????????????
        PageParams pageParams =new PageParams();
        pageParams.setPage(1);
        pageParams.setPagesize(4);
        Page<ArticleIndexView> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<ArticleIndexView> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleIndexView::getEnableIndex,1);
        //???????????????
        Page<ArticleIndexView> articleIPage = articleIndexViewMapper.selectPage(page,lambdaQueryWrapper);
        List<ArticleIndexView> records = articleIPage.getRecords();
        ArrayList<Article> objs = new ArrayList<>();
        for (ArticleIndexView record : records) {
            Article article = new Article();
            BeanUtils.copyProperties(record,article);
            objs.add(article);
        }
        List<ArticleVo> list = copyList(objs, true, true);
        return Result.success(list);
    }
    @Override
    public Result listArticleWithCount(PageParams pageParams) {
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        IPage<Article> articleIPage = articleMapper.listArticle(page,pageParams.getAuthorId(),pageParams.getTagIds(), pageParams.getYear(), pageParams.getMonth(),pageParams.getOrderRole(),pageParams.getKeyword(),pageParams.getPannel());
        long total = articleIPage.getTotal();
        List<Article> records = articleIPage.getRecords();
        List<ArticleVo> articleVoList = copyList(records, true, true);
        ListInfoVo<ArticleVo> listvos = new ListInfoVo<>();
        listvos.setResults(articleVoList);
        listvos.setLength(total);
        return Result.success(listvos);
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
        Page<ArticleIndexView> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<ArticleIndexView> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleIndexView::getEnableIndex,1);
        //???????????????
        Page<ArticleIndexView> articleIPage = articleIndexViewMapper.selectPage(page,lambdaQueryWrapper);
        List<ArticleIndexView> records = articleIPage.getRecords();
        ArrayList<Article> objs = new ArrayList<>();
        for (ArticleIndexView record : records) {
            Article article = new Article();
            BeanUtils.copyProperties(record,article);
            objs.add(article);
        }
        List<ArticleVo> list = copyList(objs, true, true);
        return Result.success(list);
    }
    @Override
    public Result getSearchTip(String keyword) {
        Page<ArticleTip> page=new Page<>(1,10);
        IPage<ArticleTip> articleIPage = articleMapper.articleTip(page,keyword);
        List<ArticleTip> records = articleIPage.getRecords();
        return Result.success(records);
    }
    @Override
    public Result getMyArticles(PageParams pageParams) {
        //?????????????????????????????????token  ???????????????????????????
        User user = UserThreadLocal.get();
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPagesize());
        IPage<Article> articlePage = articleMapper.listAllArticle(page,user.getId(),pageParams.getTagIds(),pageParams.getYear(),pageParams.getMonth(),pageParams.getOrderRole(),pageParams.getKeyword(),pageParams.getPannel());
        List<Article> records = articlePage.getRecords();
        List<ArticleVo> articleVos = copyList(records,true,true,false,user.getId());
        ListInfoVo<ArticleVo> vos = new ListInfoVo<>();
        vos.setResults(articleVos);
        vos.setLength(articlePage.getTotal());
        return Result.success(vos);
    }
    @Override
    public Result updateMyArticle(ArticleParams articleParams) {
        //?????????????????????????????????token  ???????????????????????????
        User user = UserThreadLocal.get();
        Article sourceArticle = articleMapper.selectById(articleParams.getId());
        //????????????article
        Article article = new Article();
        article.setId(articleParams.getId());
        article.setSummary(articleParams.getSummary());
        article.setArticleName(articleParams.getArticleName());
        article.setPannel(articleParams.getPannel());
        if(!articleParams.getBanner().equals("")){
            article.setBanner(articleParams.getBanner());
        }
        //????????????????????????tag
        LambdaUpdateWrapper<ArticleTag> articleTagLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleTagLambdaUpdateWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagMapper.delete(articleTagLambdaUpdateWrapper);
        List<TagVo> tags = articleParams.getTags();
        if (tags!=null){
            for (TagVo tag : tags) {
                ArticleTag articleTag=new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(article.getId());
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBody articleBody=new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParams.getBody().getContent());
        articleBody.setHtml(articleParams.getBody().getContentHtml());
        articleBody.setId(sourceArticle.getBodyId());
        articleBodyMapper.updateById(articleBody);
        articleMapper.updateById(article);
        return Result.success(null);
    }
    @Override
    public Result deleteMyArticle(String articleId) {
        User user = UserThreadLocal.get();
        articleBodyMapper.deleteById(Long.valueOf(articleId));
        articleMapper.deleteById(Long.valueOf(articleId));
        articleTagMapper.deleteById(Long.valueOf(articleId));
        return Result.success(null);
    }
    @Override
    public Result switchArticleState(String articleId) {
        User user = UserThreadLocal.get();
        Article article = articleMapper.selectById(Long.valueOf(articleId));
        Integer isPrivate = article.getIsPrivate();
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setIsPrivate(isPrivate==1?0:1);
        articleMapper.updateById(updateArticle);
        return Result.success(null);
    }



    /**
     * ????????????
     * ?????????????????????records??????????????????????????????ArticleAo
     * @param records
     * @return
     */
    //?????? ????????????????????? ????????????
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,null));
        }
        return articleVoList;
    }
    //??????????????????
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,Long userId) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record,isTag,isAuthor,isBody,userId));
        }
        return articleVoList;
    }

    /**
     * ????????????article?????? ?????????articleAo
     * @param article
     * @return
     */
    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,Long userId){

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
            //???????????????????????? ?????????????????????????????????
            LambdaQueryWrapper<ArticleUserCollect> userCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<ArticleUserLike> userLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //????????????wrapper
            userCollectLambdaQueryWrapper.eq(ArticleUserCollect::getArticleId,article.getId());
            userLikeLambdaQueryWrapper.eq(ArticleUserLike::getArticleId,article.getId());
            userCollectLambdaQueryWrapper.eq(ArticleUserCollect::getUserId,userId);
            userLikeLambdaQueryWrapper.eq(ArticleUserLike::getUserId,userId);
            //???????????????
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
