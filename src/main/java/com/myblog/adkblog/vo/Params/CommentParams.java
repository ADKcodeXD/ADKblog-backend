package com.myblog.adkblog.vo.Params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel("发送评论的参数")
public class CommentParams {
    @ApiParam("评论作者id")
    private Long authorId;
    @ApiParam("文章id")
    private Long articleId;
    @ApiParam("给某个用户的目标用户的uid")
    private Long toUid;
    @ApiParam("父级评论的id")
    private Long parentId;
    @ApiParam("评论主体")
    private String content;
}
