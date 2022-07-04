package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.adkblog.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
@TableName("bgm_calendar")
public interface UserMapper extends BaseMapper<User> {
}
