package com.myblog.adkblog.controller;

import com.myblog.adkblog.service.CommentService;
import com.myblog.adkblog.vo.CommentVo;
import com.myblog.adkblog.vo.Params.CommentParams;
import com.myblog.adkblog.vo.Params.LoginParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comment")
@Api(tags="评论相关Api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("addcomment")
    @ApiOperation("添加评论Api")
    public Result addComment(@RequestBody CommentParams commentParams){
        return commentService.addComment(commentParams);
    }

    @PostMapping("article/{id}")
    @ApiOperation("获取某文章评论Api")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id" ,value = "文章id",required = true,dataType = "Long",paramType = "params"),
        @ApiImplicitParam(name = "pageParams" ,value = "分页查询参数",required = true,dataType = "PageParams",paramType = "body")
    })
    public Result<CommentVo> getComments(@PathVariable("id")  Long id, @RequestBody PageParams pageParams){
        return commentService.findCommentByArticleId(id,pageParams);
    }
}
