package com.myblog.adkblog.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class AliossUtils {

    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.bucketName}")
    private String bucketName;
    @Value("${fileabsolutepath}")
    private String absloutePath;
    @Value("${ossurl}")
    private String ossUrl;
    public String upload(MultipartFile file, String fileName) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = sdf.format(new Date()) + "/" + fileName;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(file.getBytes()),
                objectMetadata);
        //这里改成 内网访问的二级域名+路径名(objectname)
        String url=ossUrl+objectName;
        if (ossClient != null) {
            ossClient.shutdown();
        }
        return url;
    }
    /**
     * 提供本地的路径 上传文件至oss 传完之后删除本地文件
     * @param path 本地图片路径
     * @return 返回一个图片地址
     */
    @Async("uploadImage")
    public  String uploadBgmImage(String path){
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String filename=path;
        String objectName = "bgmimage" + "/" + filename;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        OSS ossClient = new OSSClientBuilder().build("oss-cn-guangzhou.aliyuncs.com", "LTAI5t78YcsGmmK8uLsF2QFE", "IhMuAqG3KiocDyNrJYQPqLBLO2m8k4");
        File file = new File(path);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
//        PutObjectRequest putObjectRequest = new PutObjectRequest("firssst", objectName, file);
        ossClient.putObject(putObjectRequest);
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 5);
        String url = ossClient.generatePresignedUrl(bucketName, objectName, expiration).toString();
//        String url = ossClient.generatePresignedUrl("firssst", objectName, expiration).toString();
        String regex="\\?";
        String[] split = url.split(regex);
        if(ossClient!=null){
            ossClient.shutdown();
        }
        //删除文件
        file.delete();
        return split[0];
    }
    /**
     * 保存图片到本地目录(ossfs)并将其连接返回给数据库
     * @param file
     * @param fileName
     * @return
     */
    public String saveToLocalfs(MultipartFile file, String fileName) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String filePath =absloutePath+"/"+ sdf.format(new Date()) + "/" + fileName;
        //通过输入流获取图片数据
        InputStream is = file.getInputStream();
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = SaveImg.readInputStream(is);
        File fle = new File(filePath); // 本地目录
        File parentFile = fle.getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdir();
        }
        if(fle.exists()){
            //其实吧 这个分支应该不太存在额。。。
            //TODO 通过比对文件的MD5一致性 判断文件是否一致 并且返回文件
        }
        FileOutputStream fops = new FileOutputStream(fle);
        fops.write(data);
        fops.flush();
        fops.close();
        return filePath;
    }
}
