package com.neusoft.care.common.feign;

import com.neusoft.care.common.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 消息队列服务 Feign 客户端
 *
 * 调用独立的 care-mq-service（端口 8085），将业务消息投递到 RabbitMQ。
 * 供 life-service、client-service、user-auth-service 注入使用。
 *
 * 注意事项：
 * - 仅在被 @EnableFeignClients 扫描到时才创建 Bean
 * - 通过 Nacos 服务发现定位 care-mq-service 实例
 * - 消息发送失败时 Feign 默认不重试，需业务层自行容错
 *
 * @author CareCenter Team
 */
@FeignClient(value = "care-mq-service")
public interface MqServiceFeignClient {

    @PostMapping("/api/mq/internal/payment")
    Result<Void> sendPaymentNotify(@RequestBody Map<String, Object> message);

    @PostMapping("/api/mq/internal/checkin")
    Result<Void> sendCheckInNotify(@RequestBody Map<String, Object> message);

    @PostMapping("/api/mq/internal/care")
    Result<Void> sendCareNotify(@RequestBody Map<String, Object> message);
}
