package com.myblog.adkblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.PictureMapper;
import com.myblog.adkblog.pojo.Picture;
import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.service.LoginService;
import com.myblog.adkblog.service.PictureService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.utils.Myutils;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Params.PicParams;
import com.myblog.adkblog.vo.Views.PicVo;
import com.myblog.adkblog.vo.Views.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private PictureMapper pictureMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;
    @Override
    public Result getPics(PageParams pageParams) {
        Page<Picture> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<Picture> queryWrapper = new LambdaQueryWrapper<>();
        if(pageParams.getOrderRole()!=null&&pageParams.getOrderRole().equals("like")){
            queryWrapper.orderByDesc(Picture::getLikes);
        }else{
            queryWrapper.orderByDesc(Picture::getCreateTime);
        }
        if(pageParams.getTag()!=null && pageParams.getTag()!=0){
            queryWrapper.eq(Picture::getTag,pageParams.getTag());
        }
        if(pageParams.getIsOrigin()!=null){
            if(pageParams.getIsOrigin()==1){
                queryWrapper.eq(Picture::getOrigin,1);
            }
        }
        Page<Picture> pic = pictureMapper.selectPage(page, queryWrapper);
        List<Picture> records = pic.getRecords();
        try {
            ListInfoVo<PicVo> res = new ListInfoVo<>();
            res.setLength(pic.getTotal());
            List<PicVo> picVos = copyList(records);
            res.setResults(picVos);
            return Result.success(res);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(ErrorCode.API_ERROR.getCode(),ErrorCode.API_ERROR.getMsg());
        }
    }

    @Override
    public Result addPic(PicParams picParams, String token) {
        User user = loginService.checkToken(token);
        if(user==null)
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        Picture picture = new Picture();
        picture.setCreateTime(System.currentTimeMillis());
        picture.setUid(user.getId());
        picture.setSummary(picParams.getSummary());
        picture.setTitle(picParams.getTitle());
        picture.setUrl(picParams.getUrl());
        picture.setOrigin(picParams.getIsOrigin());
        picture.setTag(picParams.getTag());
        pictureMapper.insert(picture);
        return Result.success(1);
    }

    @Override
    public Result likePic(String picId) {
        Picture picture = new Picture();
        Picture pic = pictureMapper.selectById(Long.valueOf(picId));
        picture.setId(pic.getId());
        picture.setLikes(pic.getLikes()+1);
        pictureMapper.updateById(picture);
        return Result.success(1);
    }

    @Override
    public Result getMyPics(PageParams pageParams,String token) {
        User user = loginService.checkToken(token);
        if(user==null)
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        Page<Picture> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<Picture> queryWrapper = new LambdaQueryWrapper<>();
        if(pageParams.getOrderRole()!=null&&pageParams.getOrderRole().equals("like")){
            queryWrapper.orderByDesc(Picture::getLikes);
        }else{
            queryWrapper.orderByDesc(Picture::getCreateTime);
        }
        if(pageParams.getTag()!=null && pageParams.getTag()!=0){
            queryWrapper.eq(Picture::getTag,pageParams.getTag());
        }
        if(pageParams.getIsOrigin()!=null){
            if(pageParams.getIsOrigin()==1){
                queryWrapper.eq(Picture::getOrigin,1);
            }
        }
        queryWrapper.eq(Picture::getUid,user.getId());
        Page<Picture> pic = pictureMapper.selectPage(page, queryWrapper);
        List<Picture> records = pic.getRecords();
        try {
            ListInfoVo<PicVo> res = new ListInfoVo<>();
            res.setLength(pic.getTotal());
            List<PicVo> picVos = copyList(records);
            res.setResults(picVos);
            return Result.success(res);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(ErrorCode.API_ERROR.getCode(),ErrorCode.API_ERROR.getMsg());
        }
    }

    @Override
    public Result deleteMyPics(String id, String token) {
        User user = loginService.checkToken(token);
        if(user==null)
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        Picture picture = pictureMapper.selectById(Long.valueOf(id));
        if(!picture.getUid().equals(user.getId()))
            return Result.fail(ErrorCode.NO_PERMISSION.getCode(),ErrorCode.NO_PERMISSION.getMsg());
        pictureMapper.deleteById(Long.valueOf(id));
        return Result.success(null);
    }

    @Override
    public Result updateMyPics(PicParams picParams, String token) {
        User user = loginService.checkToken(token);
        if(user==null)
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        Picture picture = new Picture();
        picture.setId(Long.valueOf(picParams.getId()));
        picture.setTag(picParams.getTag());
        picture.setOrigin(picParams.getIsOrigin());
        picture.setTitle(picParams.getTitle());
        picture.setSummary(picParams.getSummary());
        pictureMapper.updateById(picture);
        return Result.success(null);
    }

    public List<PicVo> copyList(List<Picture> pictures) throws ParseException {
        List<PicVo> picVos = new ArrayList<>();
        for (Picture picture : pictures) {
            picVos.add(copy(picture));
        }
        return picVos;
    }
    public PicVo copy(Picture picture) throws ParseException {
        PicVo picVo = new PicVo();
        BeanUtils.copyProperties(picture,picVo);
        picVo.setId(picture.getId().toString());
        picVo.setCreateTime(Myutils.getTime(picture.getCreateTime()));
        UserVo userVoById = userService.findUserVoById(picture.getUid());
        picVo.setAuthor(userVoById);
        return picVo;
    }
}
