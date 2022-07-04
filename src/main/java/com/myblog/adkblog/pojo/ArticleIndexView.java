package com.myblog.adkblog.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ArticleIndexView extends Article{
    private Integer enableIndex;

    public Integer getEnableIndex() {
        return enableIndex;
    }

    public void setEnableIndex(Integer enableIndex) {
        this.enableIndex = enableIndex;
    }
}
