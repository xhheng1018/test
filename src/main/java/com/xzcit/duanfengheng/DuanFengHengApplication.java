package com.xzcit.duanfengheng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xzcit.duanfengheng.mapper")
public class DuanFengHengApplication {
    public static void main(String[] args) {
        SpringApplication.run(DuanFengHengApplication.class, args);
    }
}