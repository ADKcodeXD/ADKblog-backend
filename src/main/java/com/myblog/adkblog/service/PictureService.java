package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Params.PicParams;

public interface PictureService {
    Result getPics(PageParams pageParams);

    Result addPic(PicParams picParams, String token);

    Result likePic(String picId);

    Result getMyPics(PageParams pageParams, String token);

    Result deleteMyPics(String id, String token);

    Result updateMyPics(PicParams picParams, String token);
}
