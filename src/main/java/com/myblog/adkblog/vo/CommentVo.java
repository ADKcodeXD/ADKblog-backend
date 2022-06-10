package com.myblog.adkblog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("评论显示Vo类")
public class CommentVo {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("评论内容主体")
    private String content;
    @ApiModelProperty("用户类")
    private UserVo user;
    @ApiModelProperty("评论等级 有一级评论和二级评论")
    private Integer level;
    @ApiModelProperty("创建日期 为一个Long类型的时间戳")
    private String createDate;
    @ApiModelProperty("子级评论")
    private List<CommentVo> childrens;
    @ApiModelProperty("评论给某个用户的用户类")
    private UserVo toUser;
}
