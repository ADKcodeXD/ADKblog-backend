package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Params.BgmBrowserParams;
import com.myblog.adkblog.vo.Result;

import java.io.IOException;

public interface BgmRankService {
    Result getBrowser(BgmBrowserParams bgmBrowserParams);
}
