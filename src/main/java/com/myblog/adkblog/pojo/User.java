package com.myblog.adkblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String avatar;
    private Long date;
    private String role;
    private String username;
    private String password;
    private String email;
    private String introduce;
    private String nickname;
    private String banner;
    private Integer gender;
}
