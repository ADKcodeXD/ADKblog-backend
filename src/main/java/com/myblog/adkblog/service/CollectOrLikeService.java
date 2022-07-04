package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Views.ArticleVo;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Common.Result;

public interface CollectOrLikeService {

    Result<ListInfoVo<ArticleVo>> getMyCollect(PageParams pageParams, String token);

    Result<ListInfoVo<ArticleVo>> getMyLiked(PageParams pageParams, String token);

    Result deleteMyCollect(String articleId, String token);

    Result deleteMyLiked(String articleId, String token);
}
