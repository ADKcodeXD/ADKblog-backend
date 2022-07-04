package com.myblog.adkblog.vo.Params;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

/*
    这是用来存放pageparams参数的一个类
    vo包params用来存放post的参数体
 */
@Data
public class PageParams {
    //这个是用于分页插件的参数体，默认为第一页 每页十条数据
    @ApiParam(value = "分页当前页数 默认第一页",required = true)
    private int page=1;
    @ApiParam(value = "分页大小 默认一页十条数据",required = true)
    private int pagesize=10;
    @ApiParam("文章作者id")
    private Long authorId;
    @ApiParam("标签的id数组")
    private List<Long> tagIds;
    @ApiParam("年份 示例2022")
    private String year;
    @ApiParam("月份 实例1")
    private String month;
    @ApiParam("排序规则 按照相应的字段进行排序 或者输入a 可以倒序")
    private String orderRole;
    @ApiParam("搜索关键字")
    private String keyword;
    @ApiParam("搜索文章的板块用的")
    private Integer pannel;
    @ApiParam("这是搜索画廊用的")
    private Integer tag;
    @ApiParam("这是搜索画廊用的+1 是否原创")
    private Integer isOrigin;
}
