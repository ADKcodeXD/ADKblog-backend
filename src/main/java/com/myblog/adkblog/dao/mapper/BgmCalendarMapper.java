package com.myblog.adkblog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.adkblog.dao.mapper.dos.GroupByTime;
import com.myblog.adkblog.pojo.ArticleUserLike;
import com.myblog.adkblog.pojo.bgm.CalendarItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BgmCalendarMapper extends BaseMapper<CalendarItem> {
    List<CalendarItem> selectListByWeekDay(Integer weekday,Long stamp);
    CalendarItem selectOneById(Integer id);
    void insertOne(CalendarItem item);
    void updateOne(CalendarItem item);
}
