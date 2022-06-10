package com.myblog.adkblog.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Myutils {
    //两个工具方法
    public static int getRandomNum(int max){
        //输入10 生成[0,10)
        double res=Math.random()*max;
        int result=(int) res;
        return  result;
    }

    public static int getNumInString(String s){
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        String trim = m.replaceAll("").trim();
        int i = Integer.parseInt(trim);
        return i;
    }
}
