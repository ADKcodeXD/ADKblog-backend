package com.myblog.adkblog.vo.Params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel("获取bangumi动漫信息的参数实体类")
public class BgmBrowserParams {
    @ApiParam("类型 只接受五种 分别是web tv movie ova misc")
    private String type;
    @ApiParam("时间 格式为2022-1或者2022")
    private String airtime;
    @ApiParam("排序方式 只接受三种 rank date title")
    private String sort;
    @ApiParam("orderby 可以以某个字母为开头进行筛选 只接受a-z")
    private String order;
    @ApiParam("页数 一页最多为20条数据")
    private Integer page;
    @ApiParam("标签 可接受任意标签")
    private String tag;
    @ApiParam("大类 接受五个类型 分别为music real book anime game")
    private String bigType;
}
