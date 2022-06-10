package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.adkblog.pojo.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper  extends BaseMapper<Tag> {
    List<Tag> findTagByArticleId(Long articleid);
    Integer addTag(String tagName);
}
