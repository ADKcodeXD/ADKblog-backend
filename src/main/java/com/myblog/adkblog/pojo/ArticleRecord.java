package com.myblog.adkblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRecord {
    private Long id;
    private Long articleId;
    private Long userId;
    private Long createTime;
}
