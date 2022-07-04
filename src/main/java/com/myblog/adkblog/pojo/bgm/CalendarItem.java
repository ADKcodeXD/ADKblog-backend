package com.myblog.adkblog.pojo.bgm;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bgm_calendar")
public class CalendarItem {
    private Integer id;
    private String url;
    private Integer type;
    private String name;
    private String name_cn;
    private String summary;
    private Integer total;
    private Float score;
    private Integer rank;
    private String large;
    private String common;
    private String small;
    private String medium;
    private String grid;
    private Integer doing;
    private Long air_date;
    private Integer air_weekday;
    private String count;//直接存放json格式文件
}
