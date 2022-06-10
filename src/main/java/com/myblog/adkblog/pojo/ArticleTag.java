package com.myblog.adkblog.pojo;

import lombok.Data;

@Data
public class ArticleTag {
    private Long id;

    private Long articleId;

    private Integer tagId;
}
