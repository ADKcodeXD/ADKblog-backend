package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.dos.GroupByTime;
import com.myblog.adkblog.pojo.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleIndexMapper extends BaseMapper<Article> {
    IPage<Article> listArticle(Page<Article> page, Long authorId, List<Long> tagId, String year, String month, String orderRole);
//     Count selectCountByQuery(Long authorId, Long tagId, String year, String month, String orderRole);
}
