package com.myblog.adkblog.vo.Params;

import lombok.Data;

@Data
public class UpdateUserParams {
    private String avatar;
    private String nickname;
    private String introduce;
    private String banner;
    private String gender;
    private String email;
}
