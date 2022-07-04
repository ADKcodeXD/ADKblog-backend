package com.myblog.adkblog.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class SaveImg {

    @Value("${bangumi.agent}")
    private String bgmUserAgent; // bgm接口的agent数据

    /**
     * 传入一张网络图片 然后会自动保存到本地服务器下的/temp/xxx.jpg
     * @param img 要保存的网络图片的地址
     * @return localPath 本地存放的文件地址
     * @throws Exception
     */
    @Async("saveImg")
    public String saveImgLocal(String img) throws Exception {
        //定义一个URL对象，就是你想下载的图片的URL地址
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "10809");
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "10809");
        URL url = new URL(img);
        //打开连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent","ADKCodeXd/ADK-Blog(https://github.com/ADKcodeXD/Myblog-Vue3viteTs)");
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为10秒
        conn.setConnectTimeout(10 * 1000);
        //通过输入流获取图片数据
        InputStream is = conn.getInputStream();
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(is);
        //创建一个文件对象用来保存图片，默认保存当前工程的temp下
        String[] arrs=img.split("/");
        String fileName=arrs[arrs.length-1];
        File file = new File(fileName); // 本地目录
        FileOutputStream fops = new FileOutputStream(file);
        fops.write(data);
        fops.flush();
        fops.close();
        return fileName;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

}
