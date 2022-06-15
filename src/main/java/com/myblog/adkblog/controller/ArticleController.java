package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.ratelimit.Limit;
import com.myblog.adkblog.common.redis.Cache;
import com.myblog.adkblog.service.ArticleService;
import com.myblog.adkblog.vo.Params.ArticleParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("articles")
@Api(tags="文章相关Api")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    /**
     * 文章发布
     */
    @PostMapping("publish")
    @ApiOperation("发布文章api")
    @Limit(time = 60000,value = 1)//一分钟内只能请求一次
    public Result publish(@RequestBody ArticleParams articleParams){
        return articleService.publish(articleParams);
    }

    @PostMapping("articlelist")
    @ApiOperation("获取文章列表Api")
    @Cache(expire = 6000, name = "article")
    public Result listArticle(@RequestBody PageParams pageParams){
        return  articleService.listArticle(pageParams);
    }

    @PostMapping("articlelistcount")
    @ApiOperation("获取文章列表Api 有一个文章列表的长度")
    @Cache(expire = 1 * 60 * 1000, name = "articleListCount")//1分钟的缓存有效
    public Result listArticleWithCount(@RequestBody PageParams pageParams){
        return  articleService.listArticleWithCount(pageParams);
    }
    //文章主体
    @PostMapping("article/{id}")
    @ApiOperation("获取文章详情api")
    public Result findArticleById(@PathVariable("id")Long id,@RequestHeader("Authorization")String token){
        /**
         * 通过pathvariable来获取链接中的id值
         */
        if(StringUtils.isBlank(token)){
            token="null";
        }
        return articleService.findArticleById(id,token);
    }

    @GetMapping("indexbanner")
    @ApiOperation("获取首页轮播图数据Api")
    @Cache(name = "indexBanner")
    public Result getIndexBanner(){
        return articleService.getIndexBanner();
    }

    @GetMapping("articletime")
    @ApiOperation("获取文章归档数据Api")
    @Cache(name = "articleTime")
    public Result getGroupByTime(){
        return articleService.getGroupByTime();
    }

    @PostMapping("indexarticle")
    @ApiOperation("获取首页文章Api")
    @Cache(name = "indexArticle")
    public Result getIndexArticle(@RequestBody PageParams pageParams){
        return articleService.getIndexArticle(pageParams);
    }

    //搜索建议 只取十条
    @GetMapping("searchtip")
    @ApiOperation("获取搜索建议Api")
    @Cache(name = "searchTip")
    public Result getSearchTip(@RequestParam String keyword){
        return articleService.getSearchTip(keyword);
    }
}
