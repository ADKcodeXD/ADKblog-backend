package com.myblog.adkblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private String authorName;
    private String content;
    private String contact;
    private String avatar;
    private Long createDate;
}
