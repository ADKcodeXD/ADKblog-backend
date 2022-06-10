package com.myblog.adkblog.controller;

import com.myblog.adkblog.service.BgmRankService;
import com.myblog.adkblog.vo.BgmRankVo;
import com.myblog.adkblog.vo.ListInfoVo;
import com.myblog.adkblog.vo.Params.BgmBrowserParams;
import com.myblog.adkblog.vo.Params.CommentParams;
import com.myblog.adkblog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bgmrank")
@Api(tags="Bangumi动漫信息相关Api")
public class BgmRankController {
    @Autowired
    private BgmRankService bgmRankService;
    //利用爬虫爬取bgm首页的排行榜
    @PostMapping("getindex")
    @ApiOperation(value = "获取动漫排行榜信息Api")
    public Result<ListInfoVo<BgmRankVo>> addComment(@RequestBody BgmBrowserParams bgmBrowserParams){
        return bgmRankService.getBrowser(bgmBrowserParams);
    }
}
