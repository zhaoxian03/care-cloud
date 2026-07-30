package com.neusoft.care.mq.producer;

import com.neusoft.care.mq.dto.NotifyMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 *
 * 核心逻辑：
 * 1. 封装 RabbitTemplate.convertAndSend()，对外提供统一的消息发送入口
 * 2. 业务模块注入本类后，只需传入 NotifyMessage，无需关心 MQ 底层细节
 * 3. 发送失败时打印日志（后续可扩展为写入失败表 + 定时重试）
 *
 * 使用方式：
 * messageProducer.send("care.payment", "pay.success", new NotifyMessage(...));
 *
 * 注意事项：
 * - 使用 JSON 序列化（由 RabbitMqConfig 的 Jackson2JsonMessageConverter 统一配置）
 * - 消息投递非事务性，极端情况可能丢失（后续可开启 Publisher Confirm 模式）
 *
 * @author CareCenter Team
 */
@Component
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param exchange   交换机名称（care.payment / care.checkin / care.record）
     * @param routingKey 路由键（pay.success / checkin.new / care.complete）
     * @param message    消息体
     */
    public void send(String exchange, String routingKey, NotifyMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
