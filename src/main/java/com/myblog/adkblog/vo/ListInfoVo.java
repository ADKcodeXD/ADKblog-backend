package com.myblog.adkblog.vo;

import lombok.Data;

import java.util.List;
@Data
public class ListInfoVo<T> {

    private Long length;
    private Integer pages;
    private List<T> results;

}
