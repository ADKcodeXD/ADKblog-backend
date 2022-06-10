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
    /**
     * 作者id
     */
    private Long authorId;
    /**
     * 内容id
     */
    private Long bodyId;

}
