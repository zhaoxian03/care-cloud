package com.neusoft.care.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI服务应用主启动类
 *
 * 核心逻辑：
 * 1. Spring Boot自动配置入口，扫描并初始化所有Spring Bean
 * 2. 嵌入式Web服务器启动，暴露REST API接口
 * 3. EmbeddingSyncService通过CommandLineRunner在启动后自动执行向量同步
 *
 * @author CareCenter Team
 */
@SpringBootApplication
public class AiServiceApplication {
    /**
     * 应用入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }
}
