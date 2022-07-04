package com.myblog.adkblog.service.Impl;

import com.myblog.adkblog.dao.mapper.BgmCalendarMapper;
import com.myblog.adkblog.pojo.bgm.CalendarItem;
import com.myblog.adkblog.service.AsyncMethod;
import com.myblog.adkblog.utils.AliossUtils;
import com.myblog.adkblog.utils.Myutils;
import com.myblog.adkblog.utils.SaveImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsyncMethodImpl implements AsyncMethod {
    @Autowired
    private AliossUtils aliossUtils;
    @Autowired
    private BgmCalendarMapper bgmCalendarMapper;
    @Autowired
    private SaveImg saveImg;
    @Async("saveImg")
    public void setCalendarImg() throws Exception {
        System.out.println("线程开始-------------------------------"+Thread.currentThread().getName()+Thread.currentThread().isAlive());
        for (int i = 1; i <= 7; i++){
            Long timestamp = Myutils.getTimestamp();
            //在run方法填写要执行的操作
            List<CalendarItem> calendarItems = bgmCalendarMapper.selectListByWeekDay(i,timestamp);
            for (CalendarItem calendarItem : calendarItems) {
                //反正这个只是后台跑的线程 我知道这样好像有点shit 不过随便了
                calendarItem.setCommon(aliossUtils.uploadBgmImage(saveImg.saveImgLocal(calendarItem.getCommon())));;
                calendarItem.setLarge(aliossUtils.uploadBgmImage(saveImg.saveImgLocal(calendarItem.getLarge())));;
                calendarItem.setGrid(aliossUtils.uploadBgmImage(saveImg.saveImgLocal(calendarItem.getGrid())));;
                calendarItem.setMedium(aliossUtils.uploadBgmImage(saveImg.saveImgLocal(calendarItem.getMedium())));;
                calendarItem.setSmall(aliossUtils.uploadBgmImage(saveImg.saveImgLocal(calendarItem.getSmall())));;
                bgmCalendarMapper.updateOne(calendarItem);
            }
        }
    }

}
