package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Params.BgmBrowserParams;
import com.myblog.adkblog.vo.Common.Result;

public interface BgmRankService {
    Result getBrowser(BgmBrowserParams bgmBrowserParams);
}
