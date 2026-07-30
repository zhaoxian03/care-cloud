package com.neusoft.care.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 * 
 * 功能说明：
 * 1. 启动Spring Boot应用
 * 2. 启用Nacos服务发现
 * 3. 路由转发：/api/user/** → user-auth-service，/api/bed/** → bed-core-service
 * 
 * 端口：8080
 * 
 * @author CareCenter Team
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}