package com.myblog.adkblog.pojo;

import lombok.Data;

@Data
public class Picture {
    private Long id;
    private String url;
    private Long uid;
    private String title;
    private String summary;
    private Long createTime;
    private Integer origin;
    private Integer likes;
    private Integer tag;
}
