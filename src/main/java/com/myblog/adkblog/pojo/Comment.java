package com.myblog.adkblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long authorId;
    private Long parentId;
    private Integer level;
    private Long createDate;
    private String content;
    private Long toUid;
    private Long articleId;
}
