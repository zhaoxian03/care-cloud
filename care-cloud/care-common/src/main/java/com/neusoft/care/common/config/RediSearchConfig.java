package com.neusoft.care.common.config;

import io.redisearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RediSearch 配置类
 *
 * 核心逻辑：
 * 1. 从 Spring 配置中读取 Redis 的主机和端口（默认 localhost:6379）
 * 2. 创建 RediSearch 客户端 Bean，索引名为 "care-center-index"
 *
 * 注意事项：@Value 注解绑定的配置项来自 application.yml 或 Nacos，默认值确保本地开发可用
 *
 * @author CareCenter Team
 */
@Configuration
public class RediSearchConfig {

    /**
     * Redis 主机地址，默认 localhost
     */
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    /**
     * Redis 端口，默认 6379
     */
    @Value("${spring.redis.port:6379}")
    private int redisPort;

    /**
     * 创建 RediSearch 客户端 Bean
     *
     * 使用 care-center-index 作为固定索引名，
     * 连接信息从 Nacos 配置的 spring.redis.host/port 读取
     *
     * @return RediSearch Client 实例
     */
    @Bean
    public Client rediSearchClient() {
        return new Client("care-center-index", redisHost, redisPort);
    }
}