package com.neusoft.care.mq.consumer;

import com.neusoft.care.mq.dto.NotifyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 支付成功消息消费者
 *
 * 核心逻辑：
 * 1. 监听 care.payment.notify 队列，接收 pay.success 路由消息
 * 2. 记录支付成功日志（后续扩展：发送短信通知、APP推送、更新统计报表）
 *
 * 注意事项：
 * - 消费失败后消息会重新入队（默认 AUTO 确认模式），异常会触发重试
 * - 如需防止消息重复处理，应在业务层做幂等校验（按订单号去重）
 *
 * @author CareCenter Team
 */
@Component
public class PaymentConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    @RabbitListener(queues = "care.payment.notify")
    public void handle(NotifyMessage message) {
        log.info(">>>> [支付通知] 订单ID: {}, 客户ID: {}, 内容: {}",
                message.getBizId(), message.getCustomerId(), message.getContent());
    }
}
