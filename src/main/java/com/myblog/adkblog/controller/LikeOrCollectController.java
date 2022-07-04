package com.myblog.adkblog.controller;

import com.myblog.adkblog.service.CollectOrLikeService;
import com.myblog.adkblog.vo.Views.ArticleVo;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Common.Result;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("userset")
@Api(tags="留言板Api")
public class LikeOrCollectController {
    @Autowired
    private CollectOrLikeService collectOrLikeService;
    @PostMapping("findmycollect")
    @ApiOperation(value = "获取用户收藏Api" ,notes = "获取用户收藏Api")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "认证请求头",required = true,paramType = "String"),
            @ApiImplicitParam(name = "pageParams" ,value = "分页查询参数",required = true,dataType = "PageParams",paramType = "body")
    })
    public Result<ListInfoVo<ArticleVo>> getMyCollect(@RequestBody PageParams pageParams, @RequestHeader("Authorization")String token){
        return collectOrLikeService.getMyCollect(pageParams,token);
    }

    @PostMapping("findmylike")
    @ApiOperation(value = "获取用户所有点赞文章Api" ,notes = "获取用户点赞Api")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "认证请求头",required = true,paramType = "String"),
            @ApiImplicitParam(name = "pageParams" ,value = "分页查询参数",required = true,dataType = "PageParams",paramType = "body")
    })
    public Result<ListInfoVo<ArticleVo>> getMyLiked(@RequestBody PageParams pageParams, @RequestHeader("Authorization")String token){
        return collectOrLikeService.getMyLiked(pageParams,token);
    }

    @DeleteMapping("deletemycollect/{id}")
    @ApiOperation(value = "删除用户收藏文章Api" ,notes = "删除用户收藏文章")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "认证请求头",required = true,paramType = "String"),
            @ApiImplicitParam(name = "articleId" ,value = "文章id",required = true,dataType = "String",paramType = "Param")
    })
    public Result deleteMyCollect(@PathVariable("id") String articleId, @RequestHeader("Authorization")String token){
        return collectOrLikeService.deleteMyCollect(articleId,token);
    }

    @DeleteMapping("deletemyliked/{id}")
    @ApiOperation(value = "删除用户点赞过的文章Api" ,notes = "删除用户点赞文章")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "认证请求头",required = true,paramType = "String"),
            @ApiImplicitParam(name = "articleId" ,value = "文章id",required = true,dataType = "String",paramType = "Param")
    })
    public Result deleteMyLiked(@PathVariable("id") String articleId, @RequestHeader("Authorization")String token){
        return collectOrLikeService.deleteMyLiked(articleId,token);
    }
}
