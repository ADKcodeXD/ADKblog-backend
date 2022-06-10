package com.myblog.adkblog.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("首页文章实体类")
public class ArticeIndex {
    @ApiModelProperty("文章id")
    private Long id;
    @ApiModelProperty("文章名")
    private String articleName;

    @ApiModelProperty("描述")
    private String summary;

    @ApiModelProperty("阅读数")
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
