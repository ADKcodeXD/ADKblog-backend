package com.myblog.adkblog.service;

import com.myblog.adkblog.vo.Common.Result;

import java.text.ParseException;

public interface BgmApiService {

    Result getCalendar();

    Result getSubjectByV0(int subjectId);

    Result getSubject(int subjectId, String responseGroup, String timestamp);
}
