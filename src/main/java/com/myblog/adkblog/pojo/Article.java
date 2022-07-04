package com.myblog.adkblog.pojo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("文章实体类")
public class Article {
    private Long id;

    private String articleName;

    private String summary;

    private Integer viewCounts;

    private Integer commentCounts;

    private Integer likeCounts;

    private Integer collectCounts;

    private String banner;

    private Long createDate;

    private Long authorId;

    private Long bodyId;
    //用于筛选是否为私人文章
    private Integer isPrivate;
    //用于筛选是否为公开文章
    private Integer enable;
    //用于筛选板块
    private Integer pannel;
}
