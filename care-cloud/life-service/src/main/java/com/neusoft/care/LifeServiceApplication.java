package com.neusoft.care;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * 生活服务微服务启动类 —— life-service 应用的入口
 *
 * 核心逻辑：
 * 1. 启用 Spring Cloud 服务发现（Nacos）
 * 2. 启用 Spring Cache 缓存抽象
 * 3. 扫描 MyBatis Mapper 接口（com.neusoft.care.mapper 和 com.neusoft.care.common.mapper）
 * 4. 组件扫描覆盖 com.neusoft.care 和 com.neusoft.care.common 包
 *
 * @author CareCenter Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching  // 启用Spring Cache缓存抽象
@MapperScan({"com.neusoft.care.mapper", "com.neusoft.care.common.mapper"})
@ComponentScan(basePackages = {"com.neusoft.care", "com.neusoft.care.common"})
public class LifeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifeServiceApplication.class, args);
    }
}
