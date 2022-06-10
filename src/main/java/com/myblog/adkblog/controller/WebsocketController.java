package com.myblog.adkblog.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.myblog.adkblog.pojo.User;
import com.myblog.adkblog.pojo.WsMessage;
import com.myblog.adkblog.service.UserService;

import com.myblog.adkblog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/wschat/{username}")
@Component
public class WebsocketController {

    public static UserService userService;

    public static final Map<UserVo,Session> sessionMap=new ConcurrentHashMap<>();

    //由于单例模式和websocket的多对象模式冲突 需要这样注入service对象
    @Autowired
    public void setApplicationContext(UserService userService){
        WebsocketController.userService=userService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username){
        System.out.println(username);
        User user = userService.findUserByUsername(username);
        if(user==null){
            //连接失败 return
            return;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setId(user.getId().toString());
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();;
        sessionMap.put(userVo,session);

        WsMessage wsMessage = new WsMessage();
        wsMessage.setType(1);
        wsMessage.setUserVo(userVo);
        ArrayList<UserVo> users = new ArrayList<>();
        for (UserVo vo: sessionMap.keySet()) {
            users.add(vo);
        }

        wsMessage.setMsg(users);
        wsMessage.setCreateTime(System.currentTimeMillis());

        String json = gson.toJson(wsMessage);
        //广播 所有在线用户 处理
        boardcastAllMsg(json);
    }

    /**
     * 群聊 广播所有客户端 消息
     * @param message
     */
    private void boardcastAllMsg(String message){
        Set<UserVo> users = sessionMap.keySet();
        try {
            for (UserVo user : users) {
                Session session = sessionMap.get(user);
                session.getBasicRemote().sendText(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnMessage
    public  void  OnMessage(Session session, @PathParam("username") String username,String message){
        User user = userService.findUserByUsername(username);
        if(user==null){
            //没有找到用户 直接 return
            return;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setId(user.getId().toString());
        //服务端接收到消息
        //发送回去 群发回去
        WsMessage wsMessage = new WsMessage();
        wsMessage.setType(2);
        wsMessage.setUserVo(userVo);
        wsMessage.setMsg(message);
        wsMessage.setCreateTime(System.currentTimeMillis());
        boardcastAllMsg(new Gson().toJson(wsMessage));
    }

    @OnClose
    public void onClose(Session session,@PathParam("username") String username){
        WsMessage wsMessage = new WsMessage();
        for (UserVo userVo : sessionMap.keySet()) {
            if(userVo.getUsername().equals(username)){
                wsMessage.setUserVo(userVo);
                sessionMap.remove(userVo);
            }
        }
        wsMessage.setType(3);
        wsMessage.setMsg(username+"离开了聊天室");
        wsMessage.setCreateTime(System.currentTimeMillis());
        boardcastAllMsg(new Gson().toJson(wsMessage));
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
