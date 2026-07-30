package com.neusoft.care.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MinIO 连接配置 —— 从 Nacos（或 application.yml）读取 minio.* 配置。
 * 字段对应 Nacos 中 care-minio.yml 的内容：
 *   minio.endpoint     MinIO 服务地址
 *   minio.access-key   访问密钥
 *   minio.secret-key   密钥
 *   minio.bucket       默认 bucket
 *
 *  ConfigurationProperties 把 YAML 配置自动映射为 Java 对象，省去手动 @Value 注入。
 */
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /** MinIO 服务地址，如 http://localhost:9000 */
    private String endpoint;

    /** 访问密钥 */
    private String accessKey;

    /** 密钥 */
    private String secretKey;

    /** 默认 bucket */
    private String bucket;

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
}
