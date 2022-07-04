package com.myblog.adkblog.controller;

import com.myblog.adkblog.common.ratelimit.Limit;
import com.myblog.adkblog.utils.AliossUtils;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("upload")
@Api(tags = "上传图片Api")
public class UploadController {

    @Autowired
    private AliossUtils aliossUtils;

    @PostMapping("img")
    @ApiOperation("上传图片")
    @Limit(time = 1000,value = 3)
    public Result upload(@RequestParam("image") MultipartFile file){
        //获得文件原始名
        String originalFilename = file.getOriginalFilename();
        //使用uuid new文件名
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        String url="";
        try{
            url = aliossUtils.upload(file, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(!StringUtils.isBlank(url)){
                return Result.success(url);
            }else {
                return Result.fail(ErrorCode.UPLOAD_ERROR.getCode(),ErrorCode.UPLOAD_ERROR.getMsg());
            }
        }

    }
    @PostMapping("imglocal")
    @ApiOperation("上传图片至本地oss")
    @Limit(time = 1000,value = 3)
    public Result uploadLocal(@RequestParam("image") MultipartFile file){
        //获得文件原始名
        String originalFilename = file.getOriginalFilename();
        //使用uuid new文件名
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        String url="";
        try{
            url = aliossUtils.saveToLocalfs(file, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(!StringUtils.isBlank(url)){
                return Result.success(url);
            }else {
                return Result.fail(ErrorCode.UPLOAD_ERROR.getCode(),ErrorCode.UPLOAD_ERROR.getMsg());
            }
        }
    }
}
