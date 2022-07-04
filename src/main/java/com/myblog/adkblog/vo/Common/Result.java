package com.myblog.adkblog.vo.Common;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "返回结果集")
@Data
public class Result<T> {
    //是否请求成功状态
    private boolean success;
    //状态码
    private int code;
    //返回信息
    private String msg;
    //返回数据的主体
    private T data;


    //静态方法，当请求成功时 调用该方法返回给网页
    public static Result success(Object data){
        return new Result(true,200,"success",data);
    }

    public static Result fail(int code,String msg){
        return new Result(false,code,msg,null);
    }
}
