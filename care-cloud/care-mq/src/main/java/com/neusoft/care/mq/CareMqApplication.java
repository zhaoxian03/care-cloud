package com.neusoft.care.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 消息队列服务启动类
 *
 * 核心逻辑：
 * 1. 连接 RabbitMQ 消息中间件
 * 2. 声明交换机、队列、绑定关系（由 RabbitMqConfig 完成）
 * 3. 启动消费者监听 @RabbitListener，接收并处理各业务模块发来的异步消息
 * 4. 注册到 Nacos 服务发现，供其他服务通过 Feign 调用生产者接口
 *
 * 注意事项：
 * - 排除了 DataSource 自动配置，此服务不访问数据库
 * - 独立部署在端口 8085，与业务服务完全解耦
 *
 * @author CareCenter Team
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    MybatisPlusAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
@EnableDiscoveryClient
public class CareMqApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareMqApplication.class, args);
    }
}
