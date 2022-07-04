package com.myblog.adkblog.vo.Transfer;

import lombok.Data;

@Data
public class BgmCollections {
    private Integer doing; //在看人数
    private Integer on_hold; //搁置
    private Integer dropped; //抛弃
    private Integer collect; //看过
    private Integer wish;  //想看
}
