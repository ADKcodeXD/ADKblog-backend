package com.myblog.adkblog.vo.Params;

import lombok.Data;

@Data
public class LikeOrCollectParams {
    public String articleId;
    public Boolean flag;
}
