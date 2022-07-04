package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.dos.ArticleTip;
import com.myblog.adkblog.dao.mapper.dos.GroupByTime;
import com.myblog.adkblog.pojo.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    IPage<Article> listArticle(Page<Article> page,Long authorId, List<Long> tagIds, String year, String month,String orderRole,String keyword,Integer pannel);

    List<GroupByTime> getGroupByTime();

    IPage<ArticleTip> articleTip(Page<ArticleTip> page, String keyword);

    /**
     * 全部选择 祛去除了enable 和isprivate的选项
     * @param page
     * @param authorId
     * @param tagIds
     * @param year
     * @param month
     * @param orderRole
     * @param keyword
     * @return
     */
    IPage<Article> listAllArticle(Page<Article> page,Long authorId, List<Long> tagIds, String year, String month,String orderRole,String keyword,Integer pannel);

//     Count selectCountByQuery(Long authorId, Long tagId, String year, String month, String orderRole);
}
