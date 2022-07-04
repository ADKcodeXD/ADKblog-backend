package com.myblog.adkblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.CommentMapper;
import com.myblog.adkblog.pojo.Comment;
import com.myblog.adkblog.service.ArticleService;
import com.myblog.adkblog.service.CommentService;
import com.myblog.adkblog.service.UserService;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Params.CommentParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Views.CommentVo;
import com.myblog.adkblog.vo.Views.UserVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Override
    public Result addComment(CommentParams commentParams) {
        if(commentParams.getAuthorId()==null){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        Comment comment=new Comment();
        comment.setAuthorId(commentParams.getAuthorId());
        comment.setContent(commentParams.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent=commentParams.getParentId();
        comment.setArticleId(commentParams.getArticleId());
        if(parent==null||parent == 0 ){
            comment.setLevel(1);
        }else {
            comment.setLevel(2);
        }
        comment.setParentId(parent==null?0:parent);
        comment.setToUid(commentParams.getToUid()==null?0:commentParams.getToUid());
        commentMapper.insert(comment);
        articleService.updateViewCountsById(comment.getArticleId());
        return Result.success(null);
    }

    @Override
    public Result findCommentByArticleId(Long id,PageParams pageParams) {
        //分页插件
        Page<Comment> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //如果 指定按时间排序 那么按照倒序
        queryWrapper.eq(Comment::getArticleId,id);
        queryWrapper.eq(Comment::getLevel,1);
        if(pageParams.getOrderRole()!=null&& pageParams.getOrderRole().equals("a")){
            queryWrapper.orderByAsc(Comment::getCreateDate);
        }else {
            queryWrapper.orderByDesc(Comment::getCreateDate);
        }
        //分页 不需要条件 选择十条即可
        IPage<Comment> commentIPage = commentMapper.selectPage(page,queryWrapper);
        //获取到records
        List<Comment> records = commentIPage.getRecords();
        List<CommentVo> commentVoList=copyList(records);
        ListInfoVo<CommentVo> commentVoListInfoVo = new ListInfoVo<>();
        commentVoListInfoVo.setResults(commentVoList);
        commentVoListInfoVo.setLength(commentIPage.getTotal());
        return Result.success(commentVoListInfoVo);
    }

    @Override
    public List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        queryWrapper.orderByAsc(Comment::getCreateDate);
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList=copyList(commentList);
        return commentVoList;
    }

    private List<CommentVo> copyList(List<Comment> commentList) {
        List<CommentVo> commentVoList=new ArrayList<>();
        for (Comment comment:commentList){
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo=new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setCreateDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        commentVo.setId(comment.getId().toString());
        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = userService.findUserVoById(authorId);
        commentVo.setUser(userVo);
        //子评论
        Integer level = comment.getLevel();
        if (level==1){
            Long id2 =comment.getId();
            List<CommentVo> commentVoList=findCommentsByParentId(id2);
            commentVo.setChildrens(commentVoList);
        }
        if(level>1){
            Long toUid = comment.getToUid();
            UserVo toUser = userService.findUserVoById(toUid);
            commentVo.setToUser(toUser);
        }
        return commentVo;
    }


}
