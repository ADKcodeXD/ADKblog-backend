package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.redis.Cache;
import com.myblog.adkblog.service.SenFunService;
import com.myblog.adkblog.service.YhdmService;
import com.myblog.adkblog.vo.Common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("senfun")
@Api(tags = "senfun动漫网站爬虫")
/**
 * 暂时弃用 不好爬
 */
public class SenFunController {

    @Autowired
    private SenFunService senFunService;
    /**
     * params 分集的数据 分集连接id 然后返回一个video url
     * @return url
     */
    @GetMapping("getvideourl")
    @ApiOperation("获取senfun的搜索结果")
    @Cache(expire = 1000 * 60 * 60 * 24 * 7, name = "senfun")//由于这种数据一般不会变化 设置七天的缓存
    public Result getSearchResult(@RequestParam String keywords){
        return senFunService.getSearchResult(keywords);
//        return Result.success(null);
    }
}
