package com.myblog.adkblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class AdkblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdkblogApplication.class, args);
    }

}
