package com.myblog.adkblog.vo.Params;

import lombok.Data;

@Data
public class PicParams {
    private String id;
    private String url;
    private String title;
    private String summary;
    private Integer isOrigin;
    private Integer tag;
}
