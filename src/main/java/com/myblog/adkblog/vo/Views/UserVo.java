package com.myblog.adkblog.vo.Views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
    private String id;
    private String username;
    private String role;
    private String avatar;
    private String nickname;
    private String introduce;
    private String banner;
}
