package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.dos.GroupByTime;
import com.myblog.adkblog.pojo.Article;
import com.myblog.adkblog.pojo.ArticleIndexView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleIndexViewMapper extends BaseMapper<ArticleIndexView> {
}
