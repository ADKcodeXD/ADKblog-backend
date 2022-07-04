package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.redis.Cache;
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
@RequestMapping("yhdm")
@Api(tags = "樱花动漫爬虫Api")
public class YhdmController {
    @Autowired
    private YhdmService yhdmService;

    /**
     * params 分集的数据 分集连接id 然后返回一个video url
     * @return url
     */
    @GetMapping("getvideourl")
    @ApiOperation("获取视频URL api")
    @Cache(expire = 1000 * 60 * 60 * 24 * 7, name = "yhdm")//由于这种数据一般不会变化 设置七天的缓存
    public Result getVideoUrl(@RequestParam String url){
        return yhdmService.getVideoUrl(url);
//        return Result.success(null);
    }

    /**
     * 应该返回一个集合 然后取最相关的一个或者两个 并且找到结果 获取分集数据并返回
     * @return
     */
    @GetMapping("getsearchinfo")
    @ApiOperation("获取搜索结果 Api")
    @Cache(expire = 1000 * 60 * 60 * 24 * 7, name = "yhdm")//由于这种数据一般不会变化 设置七天的缓存
    public Result getSearchInfo(@RequestParam String keywords){
        return yhdmService.getSearchInfo(keywords);
    }
}
