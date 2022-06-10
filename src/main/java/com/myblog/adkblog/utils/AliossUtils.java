package com.myblog.adkblog.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    public String upload(MultipartFile file, String fileName) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = sdf.format(new Date()) + "/" + fileName;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(file.getBytes()), objectMetadata);
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 5);
        String url = ossClient.generatePresignedUrl(bucketName, objectName, expiration).toString();

        if (ossClient != null) {
            ossClient.shutdown();
        }

        return url;
    }
}
