package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Common.Result;

public interface SenFunService {
    Result getSearchResult(String keywords);
    Result getVideoUrl(String path);
}
