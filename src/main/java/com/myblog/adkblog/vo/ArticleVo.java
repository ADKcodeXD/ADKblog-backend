package com.myblog.adkblog.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

/**
 * 数据库中我们直接查到的数据
 * 并不能直接扔到网页中
 * 需要封装到一个vo对象中
 * 进行展示
 * 也就是要进行id到具体对象的映射
 * 直接拿到数据库中的一些id和long类型的数据，是没办法直接给别人看的，要进行id到对象的转换和映射
 */
@Data
@ApiModel("文章类")
public class ArticleVo {
    private String id;

    private String articleName;

    private String summary;

    private Integer commentCounts;

    private Integer viewCounts;

    /**
     * 创建时间
     */
    private String createDate;

    private String author;

    private UserVo authorVo;

    private String banner;

    private ArticleBodyVo body;

    private List<TagVo> tags;

    private Boolean isLiked;
    private Boolean isCollected;
    private Integer likeCounts;
    private Integer collectCounts;
}
