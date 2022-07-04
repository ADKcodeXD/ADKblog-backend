package com.myblog.adkblog.utils;

import com.alibaba.fastjson.JSON;
import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.SSLs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.adkblog.vo.Views.CalendarVo;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SpiderUtils {
    /**
     * 这是一个爬虫接口类，用于爬取bgm接口数据 以及樱花动漫或者各种其他网站的数据
     */
    @Value("${bangumi.api}")
    private String baseUrl; //bangumi的api地址
    @Value("${bangumi.agent}")
    private String bgmUserAgent; // bgm接口的agent数据

    /**
     * 用于请求接口的公共方法 只需要传入一个url就能自动进行请求
     * @param url
     */
    private Object getDataWithHttpUtils(String url){
        Header[] headers = HttpHeader.custom()
                .userAgent(bgmUserAgent)
                .build();
        try {
            HCB hcb = HCB.custom()
                    .sslpv(SSLs.SSLProtocolVersion.TLSv1_2);
            HttpClient build = hcb.build();
            String result = HttpClientUtil.get(HttpConfig.custom().url(url).client(build).headers(headers));
            Object res = JSON.parse(result);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用于获取每季新番的方法
     * @return
     */
    public ArrayList<CalendarVo> getClaendarApi() throws Exception {
        String url=baseUrl+"/calendar";
        ArrayList<CalendarVo> res = new ArrayList<>();
        //这里确实有些magic 不过大家知道每日新番是一个7size的数组就行
        ObjectMapper objectMapper = new ObjectMapper();
        Object sourceData =getDataWithHttpUtils(url);
        ArrayList arrayList = objectMapper.convertValue(sourceData, ArrayList.class);
        for(int i=0;i<arrayList.size();i++){
            ObjectMapper om2 = new ObjectMapper();
            CalendarVo item = om2.convertValue(arrayList.get(i), CalendarVo.class);
            res.add(item);
        }
        System.out.println(res);
        if(res.size() == 0){
            throw new Exception("接口请求错误");
        }else{
            return res;
        }
    }

    public Object getSubjectByPath(String path) throws Exception {
        String url=baseUrl+path;
        Object res = getDataWithHttpUtils(url);
        if(res==null){
            throw new Exception("接口请求错误");
        }else{
            return res;
        }
    }
}
