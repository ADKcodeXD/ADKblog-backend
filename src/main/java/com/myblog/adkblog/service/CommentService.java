package com.myblog.adkblog.service;

import com.myblog.adkblog.pojo.Comment;
import com.myblog.adkblog.vo.CommentVo;
import com.myblog.adkblog.vo.Params.CommentParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;

import java.util.List;

public interface CommentService {
    Result addComment(CommentParams commentParams);

    Result findCommentByArticleId(Long id, PageParams pageParams);

    List<CommentVo> findCommentsByParentId(Long id);
}
