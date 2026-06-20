package com.lostfound;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 失物招领系统启动类
 */
@SpringBootApplication
@MapperScan("com.lostfound.mapper") // 扫描MyBatis Mapper接口
public class LostfoundApplication {

    public static void main(String[] args) {
        SpringApplication.run(LostfoundApplication.class, args);
    }
}
