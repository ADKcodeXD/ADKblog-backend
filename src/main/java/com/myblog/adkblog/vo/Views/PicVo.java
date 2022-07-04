package com.myblog.adkblog.vo.Views;

import lombok.Data;

@Data
public class PicVo {
    private String url;
    private String title;
    private String id;
    private String summary;
    private UserVo author;
    private String createTime;
    private Integer origin;
    private Integer tag;
    private Integer likes;
}
