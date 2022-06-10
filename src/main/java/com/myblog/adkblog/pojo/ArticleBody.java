package com.myblog.adkblog.pojo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("文章详细")
public class ArticleBody {
    private Long id;
    private String content;
    private String html;
    private Long articleId;
}
