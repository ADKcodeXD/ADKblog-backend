package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.ratelimit.Limit;
import com.myblog.adkblog.service.TagService;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tags")
@Api(tags="标签相关Api")
public class TagController {

    @Autowired
    private TagService tagService;
    @GetMapping("all")
    @ApiOperation("获取所有标签Api")
    public Result findAll(){
        return tagService.findAll();
    }

    @GetMapping("add")
    @ApiOperation("添加标签Api")
    @Limit(time = 120000,value = 5)//两分钟内只能添加五个标签
    public Result addTag(@RequestParam String tagName){
        return tagService.addTag(tagName);
    }

    @PostMapping("taglist")
    @ApiOperation("分页获取标签Api")
    public Result getTagList(@RequestBody PageParams pageParams){
        return tagService.tagList(pageParams);
    }
}
