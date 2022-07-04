package com.myblog.adkblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.myblog.adkblog.dao.mapper.BgmCalendarMapper;
import com.myblog.adkblog.pojo.bgm.CalendarItem;
import com.myblog.adkblog.service.AsyncMethod;
import com.myblog.adkblog.service.BgmApiService;
import com.myblog.adkblog.utils.AliossUtils;
import com.myblog.adkblog.utils.Myutils;
import com.myblog.adkblog.utils.SaveImg;
import com.myblog.adkblog.utils.SpiderUtils;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Transfer.*;
import com.myblog.adkblog.vo.Views.CalendarVo;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class BgmApiServiceImpl implements BgmApiService {

    @Autowired
    private SpiderUtils spiderUtils;
    @Autowired
    private BgmCalendarMapper bgmCalendarMapper;
    @Autowired
    private AsyncMethod asyncMethod;
    @Override
    public Result getCalendar() {
        //可以预想一件事情 那就是只在1月 4月 7月 10月 接口的信息才会有更新 那么就可以在分别特定的时刻 去请求接口
        //并且保存到数据库中 (避免网络请求
        //所以就把所有的网络请求封装到一个类中 （称之为工具类 使用这个类来统一进行网络请求）
        //定义一个二维数组 常量 用于存放数据
        String ja[] = {"月耀日", "火耀日", "水耀日", "木耀日", "金耀日", "土耀日", "日耀日"};
        String cn[] = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};
        String en[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        //数据库操作
        try {
            ArrayList<CalendarVo> resList = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                WeekDay weekDay = new WeekDay();
                weekDay.setCn(cn[i - 1]);
                weekDay.setEn(en[i - 1]);
                weekDay.setJa(ja[i - 1]);
                weekDay.setId(i);
                CalendarVo res = new CalendarVo();
                res.setWeekday(weekDay);
                //这里进行数据库操作 将所有weekday一样的拿出来
                //====================================================================================
                Long timestamp = Myutils.getTimestamp();
                List<CalendarItem> calendarItems =
                        bgmCalendarMapper.selectListByWeekDay(i,timestamp);
                res.setItems(copyList(calendarItems));
                resList.add(res);
            }
            //如果数据库中没有获取到数据 或者说 时间隔得太久远了 （15天以上 那么就要去网络请求新的数据更新数据库）
            //但是由于这个需求有点玄学  那就设置为 每个月固定几天会刷新（所以这里缓存就要设置的时间短一点）
            Boolean needFlush=false;
            Calendar calendar = Calendar.getInstance();
            int date = calendar.get(Calendar.DATE);
            if(date==1||date==15||date==28||date==30||date==7||date==21){
                needFlush=true;
            }
            for (CalendarVo calendarVo : resList) {
                if(calendarVo.getItems().size()==0)
                    needFlush=true;
            }
            if(needFlush) {
                //这个是从网络获取到的最新数据 将最新的数据  全部更新到数据库中 (覆盖或者保存)
                ArrayList<CalendarVo> calendarVo = spiderUtils.getClaendarApi();
                for (CalendarVo vo : calendarVo) {
                    for (CalendarItemVo item : vo.getItems()) {
                        CalendarItem calendarItem = new CalendarItem();
                        BeanUtils.copyProperties(item,calendarItem);
                        calendarItem.setAir_date(Myutils.getTimestamp(item.getAir_date()));
                        if(item.getCollection()!=null){
                            calendarItem.setDoing(item.getCollection().getDoing());
                        }
                        if(item.getImages()!=null){
                            calendarItem.setCommon(item.getImages().getCommon());
                            calendarItem.setLarge(item.getImages().getLarge());
                            calendarItem.setGrid(item.getImages().getGrid());
                            calendarItem.setMedium(item.getImages().getMedium());
                            calendarItem.setSmall(item.getImages().getSmall());
                        }
                        if(item.getRating()!=null){
                            calendarItem.setScore(item.getRating().getScore());
                            calendarItem.setTotal(item.getRating().getTotal());
                            calendarItem.setCount(item.getRating().getCount().toString());
                        }
                        CalendarItem has = bgmCalendarMapper.selectOneById(item.getId());
                        if(has==null){
                            bgmCalendarMapper.insertOne(calendarItem);
                        }else{
                            bgmCalendarMapper.updateOne(calendarItem);
                        }
                    }
                }
//                asyncMethod.setCalendarImg();
                Result success = Result.success(calendarVo);
                return success;
            }else {
                return Result.success(resList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(ErrorCode.API_ERROR.getCode(), ErrorCode.API_ERROR.getMsg());
        }
    }

    @Override
    public Result getSubjectByV0(int subjectId) {
        String path ="/v0/subjects/" + subjectId;
        try{
            Object subjectV0 = spiderUtils.getSubjectByPath(path);
            return Result.success(subjectV0);
        }catch (Exception e){
            e.printStackTrace();
            return  Result.fail(ErrorCode.API_ERROR.getCode(), ErrorCode.API_ERROR.getMsg());
        }
    }
    @Override
    public Result getSubject(int subjectId, String responseGroup, String timestamp) {
        String path ="/subject/" + subjectId + "?" + "responseGroup=" + responseGroup;
        if (timestamp != null) {
            path += "&" + "timestamp=" + timestamp;
        }
        try {
            Object subject = spiderUtils.getSubjectByPath(path);
            return Result.success(subject);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(ErrorCode.API_ERROR.getCode(), ErrorCode.API_ERROR.getMsg());
        }
    }


    /**
     * 用于复制每一个数据库中存放的calendarItem 将其转换为页面显示的vo
     *
     * @param calendarItem
     * @return CalendarItemVo
     */
    private CalendarItemVo copy(CalendarItem calendarItem) throws ParseException {
        CalendarItemVo copyItem = new CalendarItemVo();
        BeanUtils.copyProperties(calendarItem,copyItem);
        //处理不能处理的 例如 时间戳的转换
        copyItem.setAir_date(Myutils.getTime(calendarItem.getAir_date()));
        BgmImages bgmImages = new BgmImages();
        bgmImages.setCommon(calendarItem.getCommon());
        bgmImages.setGrid(calendarItem.getGrid());
        bgmImages.setLarge(calendarItem.getLarge());
        bgmImages.setMedium(calendarItem.getMedium());
        bgmImages.setSmall(calendarItem.getSmall());
        copyItem.setImages(bgmImages);
        Rating rating = new Rating();
        rating.setCount(JSON.parse(calendarItem.getCount()));
        rating.setScore(calendarItem.getScore());
        rating.setTotal(calendarItem.getTotal());
        copyItem.setRating(rating);
        BgmCollections bgmCollections = new BgmCollections();
        bgmCollections.setDoing(calendarItem.getDoing());
        copyItem.setCollection(bgmCollections);
        return copyItem;
    }

    private List<CalendarItemVo> copyList(List<CalendarItem> calendarItemList) throws ParseException {
        ArrayList<CalendarItemVo> res = new ArrayList<>();
        for (CalendarItem calendarItem : calendarItemList) {
            res.add(copy(calendarItem));
        }
        return  res;
    }

}
