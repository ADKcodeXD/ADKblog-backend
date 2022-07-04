package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.pojo.Article;
import com.myblog.adkblog.pojo.Picture;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureMapper extends BaseMapper<Picture> {
}
