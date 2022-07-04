package com.myblog.adkblog.vo.Params;

import com.myblog.adkblog.vo.Views.TagVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("发布文章参数model")
public class ArticleParams {
    @ApiModelProperty("文章id 设置错了")
    private Long id;
    @ApiModelProperty("文章详情 content 和html部分 需要分别获取")
    private ArticleBodyParams body;
    @ApiModelProperty("文章摘要 必须")
    private String summary;
    @ApiModelProperty("文章标签的集合 为一个数组")
    private List<TagVo> tags;
    @ApiModelProperty("文章名")
    private String articleName;
    @ApiModelProperty("文章头图的链接 是url")
    private String banner;
    @ApiModelProperty("文章板块 pannel")
    private Integer pannel;
}
