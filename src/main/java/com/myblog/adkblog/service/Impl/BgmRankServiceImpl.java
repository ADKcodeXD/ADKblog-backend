package com.myblog.adkblog.service.Impl;

import com.myblog.adkblog.service.BgmRankService;
import com.myblog.adkblog.vo.BgmRankVo;
import com.myblog.adkblog.vo.ErrorCode;
import com.myblog.adkblog.vo.ListInfoVo;
import com.myblog.adkblog.vo.Params.BgmBrowserParams;
import com.myblog.adkblog.vo.Result;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.myblog.adkblog.utils.Myutils.getNumInString;
import static com.myblog.adkblog.utils.Myutils.getRandomNum;


@Service
public class BgmRankServiceImpl implements BgmRankService {
    private String baseUrl="https://bgm.tv/anime/browser";
//    private String baseUrl="https://baidu.com";
    private String[] userAgents={"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:83.0) Gecko/20100101 Firefox/83.0",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36",
    "Mozilla/5.0 (Linux; Android 10; HLK-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.92 Mobile Safari/537.36",
    "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36"};
    @Override
    public Result getBrowser(BgmBrowserParams bgmBrowserParams) {
        //获取首页以及排名等
        //拼接规则 type 直接是path拼接上去 如果有airtime 则加上airtimepath 后面加上参数
        //也就是:https://bgm.tv/anime/browser/(type)/(airtime)/(airtime-value)?(sort=?)&(page=?)&(orderby=?)

        //3.24 更新 前面的anime 其实是一个大类 有anime real music等
        //还需要改掉这个大类别
        ArrayList<BgmRankVo> results = new ArrayList<>();
        ListInfoVo<BgmRankVo> listInfoVo = new ListInfoVo<>();
        try {
            String url=baseUrl;
            if(bgmBrowserParams.getBigType()!=null){
                url=String.format("https://bgm.tv/%s/browser,",bgmBrowserParams.getBigType());
            }
            if(bgmBrowserParams.getTag()!=null){
                //需要去掉browser 并且加上/tag/? 并且这里的animetype是不起作用的
                url=url.replace("browser","tag/");
                url+=bgmBrowserParams.getTag();
            }
            if(bgmBrowserParams.getType()!=null){
                url+="/"+bgmBrowserParams.getType();
            }
            if(bgmBrowserParams.getAirtime()!=null){
                url+="/airtime/"+bgmBrowserParams.getAirtime();
            }
            Connection connection = Jsoup.connect(url);
            connection.userAgent(userAgents[getRandomNum(userAgents.length)]);
            connection.timeout(8000);
            if(bgmBrowserParams.getPage()!=null){
                connection.data("page",bgmBrowserParams.getPage().toString());
            }
            if(bgmBrowserParams.getOrder()!=null){
                connection.data("orderby",bgmBrowserParams.getOrder());
            }
            if(bgmBrowserParams.getSort()!=null){
                connection.data("sort",bgmBrowserParams.getSort());
            }
            Document doc = connection.get();
            //开始爬虫
            Element browserItemList = doc.getElementById("browserItemList");
            Elements items = browserItemList.getElementsByTag("li");
            for (Element item : items) {
                BgmRankVo bgmRankVo = new BgmRankVo();
                bgmRankVo.setId(Integer.parseInt(item.attr("id").split("_")[1]));
                //获取下面的a标签
                Element imgAtag = item.getElementsByTag("a").get(0);
                String src = imgAtag.select(".image img").attr("src");
                //处理图片链接
                StringBuffer sb=new StringBuffer(src);
                sb.insert(0,"https:");
                bgmRankVo.setImageUrl(sb.toString());
                //获取信息
                Elements innerTitle = item.select(".inner h3");
                Elements small = innerTitle.select("small");
                String name="";
                String nameCn="";
                if(!small.isEmpty()){
                    name=small.get(0).text();
                    nameCn=innerTitle.select("a").get(0).text();
                }else{
                    //只有原名 没有译名
                    name=innerTitle.get(0).text();
                }
                bgmRankVo.setNameCn(nameCn);
                bgmRankVo.setName(name);
                //获取排名
                Elements rank = item.select(".inner .rank");
                if(!rank.isEmpty()){
                    String rankText = rank.text();
                    int rankReal = getNumInString(rankText);
                    bgmRankVo.setRank(rankReal);
                }
                //获取缩略信息
                Elements info = item.select(".inner .info");
                if(!info.isEmpty()){
                    String infoDetail = info.get(0).text();
                    bgmRankVo.setInfoTip(infoDetail);
                }
                //获取评分
                Elements rate = item.select(".inner .rateInfo");
                if(!rate.isEmpty()){
                    Elements score = rate.select(".fade");
                    if(!score.isEmpty()){
                        bgmRankVo.setScore(Double.parseDouble(score.get(0).text()));
                    }
                    Elements counts = rate.select(".tip_j");
                    if(!counts.isEmpty()){
                        int count = getNumInString(counts.get(0).text());
                        bgmRankVo.setCount(count);
                    }
                }
                results.add(bgmRankVo);
            }
            //爬取页数
            int pageTotal=0;
            Elements page_inner = doc.getElementsByClass("page_inner");
            if(!page_inner.isEmpty()){
                String[] num = page_inner.get(0).text().split("/");
                if(num.length==1){
                    //少于10页 直接获取>>后的
                    char a=num[0].charAt(num[0].indexOf('›')-1);
                    int i = Integer.parseInt(String.valueOf(a));
                    pageTotal=i;
                }else{
                    pageTotal =getNumInString(num[1]);
                }
            }
            listInfoVo.setPages(pageTotal);
        }catch (IOException e){
            System.out.println(e);
            return Result.fail(ErrorCode.TIME_OUT.getCode(),ErrorCode.TIME_OUT.getMsg());
        }
        listInfoVo.setResults(results);
        return Result.success(listInfoVo);
    }





}
