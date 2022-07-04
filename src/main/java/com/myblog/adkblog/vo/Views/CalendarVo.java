package com.myblog.adkblog.vo.Views;

import com.myblog.adkblog.vo.Transfer.CalendarItemVo;
import com.myblog.adkblog.vo.Transfer.WeekDay;
import lombok.Data;

import java.util.List;

@Data
public class CalendarVo {
    private List<CalendarItemVo> items;
    private WeekDay weekday;
}
