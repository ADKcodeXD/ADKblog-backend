package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.dos.ArticleTip;
import com.myblog.adkblog.dao.mapper.dos.Count;
import com.myblog.adkblog.dao.mapper.dos.GroupByTime;
import com.myblog.adkblog.pojo.Article;
import com.myblog.adkblog.vo.Result;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    IPage<Article> listArticle(Page<Article> page,Long authorId, List<Long> tagIds, String year, String month,String orderRole,String keyword);

    List<GroupByTime> getGroupByTime();

    IPage<ArticleTip> articleTip(Page<ArticleTip> page, String keyword);

//     Count selectCountByQuery(Long authorId, Long tagId, String year, String month, String orderRole);
}
