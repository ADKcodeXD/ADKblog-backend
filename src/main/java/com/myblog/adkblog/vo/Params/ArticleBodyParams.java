package com.myblog.adkblog.vo.Params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("文章详细信息参数")
public class ArticleBodyParams {
    @ApiModelProperty("文章内容 content 非html格式")
    private String content;
    @ApiModelProperty("文章内容html格式")
    private String contentHtml;
}
