package com.myblog.adkblog.vo;

import lombok.Data;

import java.util.List;

@Data
public class YhdmSearchVo {
    private String title;
    private List<Epinfo> epInfo;
}
