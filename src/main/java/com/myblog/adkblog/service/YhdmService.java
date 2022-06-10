package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Result;

public interface YhdmService {
    Result getSearchInfo(String keywords);

    Result getVideoUrl(String url);
}
