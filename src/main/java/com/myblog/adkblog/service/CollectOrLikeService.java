package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.ArticleVo;
import com.myblog.adkblog.vo.ListInfoVo;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;

public interface CollectOrLikeService {

    Result<ListInfoVo<ArticleVo>> getMyCollect(PageParams pageParams, String token);

    Result<ListInfoVo<ArticleVo>> getMyLiked(PageParams pageParams, String token);

    Result deleteMyCollect(String articleId, String token);

    Result deleteMyLiked(String articleId, String token);
}
