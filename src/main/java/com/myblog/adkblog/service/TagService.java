package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Views.TagVo;

import java.util.List;

public interface TagService {

    Result findAll();

    List<TagVo> findTagByArticleId(Long articleid);

    Result addTag(String tagName);

    Result tagList(PageParams pageParams);
}
