package com.myblog.adkblog.vo;

import lombok.Data;

@Data
public class BgmRankVo {
    private Integer id;
    private String name;
    private String nameCn;
    private Integer rank;
    private String infoTip;
    private double score;
    private Integer count;
    private String imageUrl;
}
