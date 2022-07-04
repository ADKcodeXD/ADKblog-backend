package com.myblog.adkblog.vo.Views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAllVo {
    private String id;
    private String avatar;
    private String date;
    private String role;
    private String username;
    private String email;
    private String introduce;
    private String nickname;
    private String banner;
    private Integer gender;
}
