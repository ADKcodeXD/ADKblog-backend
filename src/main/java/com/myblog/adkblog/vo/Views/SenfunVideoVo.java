package com.myblog.adkblog.vo.Views;

import com.myblog.adkblog.vo.Common.Epinfo;
import lombok.Data;

import java.util.List;

@Data
public class SenfunVideoVo {
    String epTitle;
    String epUrl;
    List<Epinfo> epinfoList;
}
