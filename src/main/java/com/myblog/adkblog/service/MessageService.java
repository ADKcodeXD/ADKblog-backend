package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Params.MessageParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Common.Result;

public interface MessageService {

    Result addMessage(MessageParams messageParams);

    Result getMessage(PageParams pageParams);
}
