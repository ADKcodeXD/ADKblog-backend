package com.myblog.adkblog.pojo;

import com.myblog.adkblog.vo.Views.UserVo;
import lombok.Data;

@Data
public class WsMessage<T> {
    //type 1系统消息 2群聊消息
    private Integer type;
    //如果为null 前端处理
    private UserVo userVo;
    private T msg;
    private Long createTime;
}
