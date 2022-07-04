package com.myblog.adkblog.vo.Transfer;

import lombok.Data;

@Data
public class CalendarItemVo {
    private Integer id;
    private String url;
    private Integer type;
    private String name;
    private String name_cn;
    private String summary;
    private String air_date;
    private Integer air_weekday;
    private Integer rank;
    private Rating rating;
    private BgmImages images;
    private BgmCollections collection;
}
