package com.myblog.adkblog.controller;


import com.myblog.adkblog.common.ratelimit.Limit;
import com.myblog.adkblog.common.redis.Cache;
import com.myblog.adkblog.service.PictureService;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Params.PicParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pic")
@Api(tags="画廊API")
public class PictureController {
    @Autowired
    private PictureService pictureService;
    /**
     * params 分页参数
     * @return 一个列表
     */
    @PostMapping("getPics")
    @ApiOperation("获取图片by分页参数")
    @Cache(expire = 5000 ,name = "piclist")
    public Result getPics(@RequestBody PageParams pageParams){
        return pictureService.getPics(pageParams);
    }

    @PostMapping("addPic")
    @ApiOperation("添加图片")
    @Limit(time = 10000,value = 2)
    public Result addPic(@RequestBody PicParams picParams, @RequestHeader("Authorization")String token){
        return pictureService.addPic(picParams,token);
    }

    @GetMapping("likePic")
    @ApiOperation("点赞图片")
    @Limit(time = 60000,value = 20)
    public Result likePic(@Param("id") String id){
        return pictureService.likePic(id);
    }

    @PostMapping("getMyPics")
    @ApiOperation("获取自己发布的图片")
    public Result getMyPics(@RequestBody PageParams pageParams,@RequestHeader("Authorization")String token){
        return pictureService.getMyPics(pageParams,token);
    }

    @PostMapping("deleteMyPic/{id}")
    @ApiOperation("删除自己发布的图片")
    public Result deleteMyPics(@PathVariable("id")String id,@RequestHeader("Authorization")String token){
        return pictureService.deleteMyPics(id,token);
    }

    @PostMapping("updateMyPic")
    @ApiOperation("修改图片信息 无法改变图片地址！")
    public Result updateMyPics(@RequestBody PicParams picParams,@RequestHeader("Authorization")String token){
        return pictureService.updateMyPics(picParams,token);
    }
}
