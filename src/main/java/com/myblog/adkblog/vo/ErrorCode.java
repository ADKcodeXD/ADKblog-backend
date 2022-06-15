package com.myblog.adkblog.vo;

public enum  ErrorCode {
    /**
     * 枚举各种不同的错误码
     * 以方便进行错误信息提醒
     */
    PARAMS_ERROR(10001, "参数有误"),
    ACCOUNT_PWD_NOT_EXIST(10002, "用户名或密码不存在"),
    TOKEN_ERROR(10003, "token不合法"),
    ACCOUNT_EXIST(10004, "账号已存在"),
    NO_PERMISSION(70001, "无访问权限"),
    SESSION_TIME_OUT(90001, "会话超时"),
    NO_LOGIN(90002, "该功能需要登录才能使用"),
    TIME_OUT(40004, "请求超时 请刷新或者重试"),
    UPLOAD_ERROR(20001, "上传失败"),
    IS_LOGINING(10005, "您已经登录了~"),
    RATE_LIMIT(48484, "你好像请求的次数太快了~休息一会吧");

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private int code;
    private String msg;
}
