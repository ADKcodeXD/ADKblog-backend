package com.myblog.adkblog.service.Impl;

import com.myblog.adkblog.service.SenFunService;
import com.myblog.adkblog.utils.Myutils;
import com.myblog.adkblog.vo.Common.Epinfo;
import com.myblog.adkblog.vo.Common.ErrorCode;
import com.myblog.adkblog.vo.Common.Result;
import com.myblog.adkblog.vo.Views.SenfunSearchVo;
import com.myblog.adkblog.vo.Views.SenfunVideoVo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class SenFunServiceImpl implements SenFunService {
    private String searchUrl="https://senfun.net/search.html?wd=";
    private String baseUrl="https://senfun.net";
    @Override
    public Result getSearchResult(String keywords) {
        try {
            Connection connection = Jsoup.connect(searchUrl+keywords);
            connection.userAgent(Myutils.getRandomAgent());
            connection.timeout(8000);
            Document doc = connection.get();
            Elements moduleMain = doc.select(".module .module-main .module-items");
            ArrayList<SenfunSearchVo> senfuns = new ArrayList<>();
            if(!moduleMain.isEmpty()){
                Element element = moduleMain.get(0);
                Elements items = element.select(".module-card-item");
                if(!items.isEmpty()){
                    for (Element item : items) {
                        //这里就是所有的搜索结果 把他们存进一个类中
                        SenfunSearchVo senfunSearchVo = new SenfunSearchVo();
                        Element tag = item.selectFirst(".module-card-item-class");
                        Element infolist = item.selectFirst(".module-card-item-info");
                        Element title = infolist.selectFirst(".module-card-item-title");
                        Element a = item.selectFirst(".module-card-item-footer");
                        Element watchlink = a.selectFirst("a");
                        senfunSearchVo.setLink(watchlink.attr("href"));
                        senfunSearchVo.setTag(tag.text());
                        senfunSearchVo.setTitle(title.text());
                        senfuns.add(senfunSearchVo);
                    }
                }
            }
            return Result.success(senfuns);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(ErrorCode.API_ERROR.getCode(),ErrorCode.API_ERROR.getMsg());
        }
    }

    /**
     * 这里由于senfun使用了一点加密手段 仅凭我的爬虫能力实在是无法胜任该爬取工作 所以暂时先摆了。
     * @param path
     * @return
     */
    @Override
    public Result getVideoUrl(String path){
        try {
            Connection connection = Jsoup.connect(baseUrl+path);
            connection.userAgent(Myutils.getRandomAgent());
            connection.timeout(10000);
            Document doc = connection.get();
            //获取播放分集
            Element playlist = doc.selectFirst(".module .module-main .module-player-side");
            Element playlistcontainer = playlist.selectFirst(".player-list");
            Element pannel = playlistcontainer.selectFirst(".module-list");
            Elements select = pannel.select(".module-play-list-link");
            SenfunVideoVo senfun = new SenfunVideoVo();
            List<Epinfo> eplist = new ArrayList<>();
            for (Element element : select) {
                Epinfo epinfo = new Epinfo();
                epinfo.setEpTitle(element.text());
                epinfo.setEpUrl(element.attr("href"));
                eplist.add(epinfo);
            }
            senfun.setEpinfoList(eplist);
            Element element = doc.selectFirst(".module .module-main .player-box .player-box-main");
//            Element iframe = element.getElementById("install");
            System.out.println(element);
            System.out.println(senfun);
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(ErrorCode.API_ERROR.getCode(),ErrorCode.API_ERROR.getMsg());
        }
    }
    public static void main(String[] args) {
        new SenFunServiceImpl().getVideoUrl("/watch/427/2/1.html");
    }
}
