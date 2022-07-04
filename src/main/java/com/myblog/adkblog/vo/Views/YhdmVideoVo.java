package com.myblog.adkblog.vo.Views;

import com.myblog.adkblog.vo.Common.Epinfo;
import lombok.Data;

import java.util.List;

@Data
public class YhdmVideoVo {
    private String title;
    private String videoUrl;
    private List<Epinfo> epInfo;
}
