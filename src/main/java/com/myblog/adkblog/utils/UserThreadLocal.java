package com.myblog.adkblog.utils;

import com.myblog.adkblog.pojo.User;

/**
 * 这是一个用于存放对象的一个类
 * 能够将拿到的对象长期存放在这当中，
 * 我们可以快速获取该对象
 */
public class UserThreadLocal {
    private UserThreadLocal(){}

    private static final ThreadLocal<User> LOCAL=new ThreadLocal<>();

    public static void put(User user){
        LOCAL.set(user);
    }
    public static User get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}
