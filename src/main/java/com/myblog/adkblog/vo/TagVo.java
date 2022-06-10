package com.myblog.adkblog.vo;

import lombok.Data;

@Data
public class TagVo {
//    @JsonSerialize(using = ToStringSerializer.class)
    private Integer id;

    private String tagName;

}
