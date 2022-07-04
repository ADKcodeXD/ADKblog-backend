package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.redis.Cache;
import com.myblog.adkblog.service.BgmApiService;
import com.myblog.adkblog.vo.Common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bgm")
@Api(tags = "用于直接请求Bangumi接口 并支持缓存")
public class BgmDataController {
    @Autowired
    private BgmApiService bgmApiService;

    @GetMapping("calendar")
    @ApiOperation("获取每日更新番剧")
    @Cache(expire = 1000 * 60 * 60 * 24 * 1, name = "bgmCalendar") //一天过期 防止换季更新不及时
    public Result getCalendar() {
        return bgmApiService.getCalendar();
    }

    @GetMapping("subject/{id}")
    @ApiOperation("获取条目的简略信息")
    @Cache(expire = 1000 * 60 * 60 * 24 * 1, name = "bgmSubject") //也是一天过期 不过其实可以很久才过期 没啥必要一天 为了减少缓存吧
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "条目id", required = true, dataType = "int", paramType = "params"),
    })
    public Result getSubjectEasy(@PathVariable("id") int subjectId) {
        return bgmApiService.getSubjectByV0(subjectId);
    }

    @GetMapping("subjectAll/{id}")
    @ApiOperation("获取条目的所有信息")
    @Cache(expire = 1000 * 60 * 60 * 24 * 1, name = "bgmSubjectAll") //也是一天过期 不过其实可以很久才过期 没啥必要一天 为了减少缓存吧
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "条目id", required = true, dataType = "int", paramType = "Path"),
            @ApiImplicitParam(name = "responseGroup", value = "定义返回大小", required = true, dataType = "String", paramType = "param"),
            @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "String", paramType = "param")
    })
    public Result getSubjectAll(@PathVariable("id") int subjectId,
                                @Param("responseGroup") String responseGroup,
                                @Param("timestamp") String timestamp) {
        return bgmApiService.getSubject(subjectId, responseGroup, timestamp);
    }
}
