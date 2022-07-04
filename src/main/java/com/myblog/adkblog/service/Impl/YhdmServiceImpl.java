package com.myblog.adkblog.service.Impl;

import com.myblog.adkblog.service.YhdmService;
import com.myblog.adkblog.utils.Myutils;
import com.myblog.adkblog.vo.Common.Epinfo;
import com.myblog.adkblog.vo.Common.ListInfoVo;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Views.YhdmSearchVo;
import com.myblog.adkblog.vo.Views.YhdmVideoVo;
import lombok.Data;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.myblog.adkblog.utils.Myutils.getNumInString;
import static com.myblog.adkblog.utils.Myutils.getRandomNum;

@Service
public class YhdmServiceImpl implements YhdmService {

    private String searchUrl="http://www.yinghuacd.com/search/";
    private String baseUrl="http://www.yinghuacd.com/";
    @Override
    public Result getSearchInfo(String keywords) {
        String url=searchUrl+keywords;
        ArrayList<YhdmSearchVo> res = new ArrayList<>();
        ListInfoVo<YhdmSearchVo> resultList = new ListInfoVo<>();
        try {
            Connection connection = Jsoup.connect(url);
            connection.userAgent(Myutils.getRandomAgent());
            connection.timeout(8000);
            Document doc = connection.get();
            Elements lpic = doc.select(".area .fire .lpic");
            //获取到下面的ul
            Element element = lpic.get(0);
            Elements lpicul = element.getElementsByTag("ul");
            ArrayList<TempDos> itemUrls = new ArrayList<>();
            //获取搜索结果前三关联的条目
            //备注 由于樱花动漫的搜索逻辑是 最相关的在前面 所以需要获取分页数据 找到最后一页并且倒序插入
            //每一页结果为20条
            Elements pages = lpic.get(0).getElementsByClass("pages");
            if(!pages.isEmpty()){
                Element totalnum = pages.get(0).getElementById("totalnum");
                int total = getNumInString(totalnum.text());
                int page= (int) Math.ceil(total/20.0);
                //重新发起请求
                url=searchUrl+keywords+"/?page="+page;
                connection = Jsoup.connect(url);
                connection.userAgent(Myutils.getRandomAgent());
                connection.timeout(8000);
                doc = connection.get();
                lpic = doc.select(".area .fire .lpic");
                lpicul = lpic.get(0).getElementsByTag("ul");
            }
            if(!lpicul.isEmpty()){
                //如果是有分页数据的 我们需要到最后一页去寻找相应的搜索结果

                Elements lis = lpicul.get(0).getElementsByTag("li");
                if(!lis.isEmpty()) {
                    int count=lis.size();
                    if(count>5) count=5;
                    int size=lis.size();
                    //搜索三条关联条目
                    for(int i=0;i<count;i++){
                        TempDos tempDos = new TempDos();
                        Element item = lis.get(size-i-1);
                        Elements h2 = item.getElementsByTag("h2");
                        if(!h2.isEmpty()){
                            Elements a1 = h2.get(0).getElementsByTag("a");
                            String url1 = a1.get(0).attr("href");
                            String title1 = a1.get(0).attr("title");
                            tempDos.setTitle(title1);
                            tempDos.setUrl(baseUrl+url1);
                        }
                        itemUrls.add(tempDos);
                    }

                }
            }

            if(itemUrls.size()>0){
                for (TempDos itemUrl : itemUrls) {
                    YhdmSearchVo yhdmSearchVo = new YhdmSearchVo();
                    yhdmSearchVo.setTitle(itemUrl.title);
                    Connection connection1 = Jsoup.connect(itemUrl.getUrl());
                    connection1.userAgent(Myutils.getRandomAgent());
                    connection1.timeout(8000);
                    Document doc2 = connection1.get();
                    Elements movurl = doc2.getElementsByClass("movurl");
                    ArrayList<Epinfo> eplist = new ArrayList<>();
                    if(!movurl.isEmpty()){
                        //获取到ul
                        Elements ul = movurl.get(0).getElementsByTag("ul");
                        if(!ul.isEmpty()){
                            Elements lis = ul.get(0).getElementsByTag("li");
                            if(!lis.isEmpty()){
                                for (Element li : lis) {
                                    Epinfo epinfo = new Epinfo();
                                    Elements a = li.getElementsByTag("a");
                                    if(!a.isEmpty()){
                                        Element a1 = a.get(0);
                                        String epUrl = a1.attr("href");
                                        epinfo.setEpTitle(a1.text());
                                        epinfo.setEpUrl(epUrl);
                                        eplist.add(epinfo);
                                    }
                                }
                            }
                        }
                    }
                    yhdmSearchVo.setEpInfo(eplist);
                    res.add(yhdmSearchVo);
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        resultList.setResults(res);
        resultList.setLength((long) res.size());
        return Result.success(resultList);
    }

    @Override
    public Result getVideoUrl(String url) {
        //传进来的是 5479-2的样子  把分集数据和视频url一同返回过去
        String connectUrl=baseUrl+"/v/"+url+".html";
        YhdmVideoVo yhdmVideoVo = new YhdmVideoVo();
        try {
            Connection connection = Jsoup.connect(connectUrl);
            connection.userAgent(Myutils.getRandomAgent());
            connection.timeout(8000);
            Document doc=connection.get();
            Elements select = doc.select(".bofang div");
            if(!select.isEmpty()){
                Element div = select.get(0);
                String videoUrl = div.attr("data-vid");
                String[] urls = videoUrl.split("$");
                yhdmVideoVo.setVideoUrl(urls[0]);
            }
            Elements ul = doc.select(".movurls ul");
            if(!ul.isEmpty()){
                Elements li = ul.get(0).getElementsByTag("li");
                ArrayList<Epinfo> eplist = new ArrayList<>();
                if(!li.isEmpty()){
                    for (Element element : li) {
                        Epinfo epinfo = new Epinfo();
                        epinfo.setEpTitle(element.text());
                        epinfo.setEpUrl(element.getElementsByTag("a").attr("href"));
                        eplist.add(epinfo);
                    }
                    yhdmVideoVo.setEpInfo(eplist);
                }
            }
            Elements select1 = doc.select(".gohome");
            if(!select1.isEmpty()){
                Elements h1 = select1.get(0).getElementsByTag("h1");
                if(!h1.isEmpty()){
                    String animename = h1.get(0).text();
                    yhdmVideoVo.setTitle(animename);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success(yhdmVideoVo);
    }

    @Data
    class TempDos{
        String url;
        String title;
    }

}
