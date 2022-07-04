package com.myblog.adkblog.vo.Views;

import com.myblog.adkblog.vo.Common.Epinfo;
import lombok.Data;

import java.util.List;

@Data
public class YhdmSearchVo {
    private String title;
    private List<Epinfo> epInfo;
}
