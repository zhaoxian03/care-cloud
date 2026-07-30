package com.neusoft.care.common.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置 —— 根据 MinioProperties 构建全局唯一的 MinioClient Bean。
 * MinioService 通过注入 MinioClient 来执行上传/下载操作。
 * 添加 @ConditionalOnProperty，无 minio.endpoint 配置的服务自动跳过。  //智能开关
 */
@Configuration
@ConditionalOnProperty(prefix = "minio", name = "endpoint")
public class MinioConfig {

    /**
     * 创建 MinioClient 实例，注入到 Spring 容器中。
     * 每个应用只创建一个客户端，复用连接。
     */
    @Bean
    public MinioClient minioClient(MinioProperties props) {
        return MinioClient.builder()
                .endpoint(props.getEndpoint())
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }
}
