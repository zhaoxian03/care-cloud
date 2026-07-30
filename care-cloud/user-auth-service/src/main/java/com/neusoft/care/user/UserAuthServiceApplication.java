package com.neusoft.care.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户认证服务启动类
 * 
 * 功能说明：
 * 1. 启动Spring Boot应用
 * 2. 启用Nacos服务发现
 * 3. 扫描Mapper接口
 * 4. 扫描common模块的组件
 * 
 * 端口：8081
 * 
 * @author CareCenter Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching  // 启用Spring Cache缓存抽象
@MapperScan({"com.neusoft.care.user.mapper", "com.neusoft.care.common.mapper"})
@ComponentScan(basePackages = {"com.neusoft.care.user", "com.neusoft.care.common"})
public class UserAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthServiceApplication.class, args);
    }
}