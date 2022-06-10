package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Params.ArticleParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;

public interface ArticleService {
    Result listArticle(PageParams pageParams);

    Result publish(ArticleParams articleParams);

    Result findArticleById(Long id,String token);

    Result getIndexBanner();

    Result listArticleWithCount(PageParams pageParams);

    void updateViewCountsById(Long id);

    Result getGroupByTime();

    Result getIndexArticle(PageParams pageParams);

    Result getSearchTip(String keyword);
}
