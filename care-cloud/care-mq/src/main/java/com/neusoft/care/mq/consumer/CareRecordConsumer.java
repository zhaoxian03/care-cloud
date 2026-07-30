package com.neusoft.care.mq.consumer;

import com.neusoft.care.mq.dto.NotifyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 护理完成消息消费者
 *
 * 核心逻辑：
 * 1. 监听 care.record.notify 队列，接收 care.complete 路由消息
 * 2. 记录护理完成日志（后续扩展：生成护理报告、通知家属）
 *
 * @author CareCenter Team
 */
@Component
public class CareRecordConsumer {
    private static final Logger log = LoggerFactory.getLogger(CareRecordConsumer.class);

    @RabbitListener(queues = "care.record.notify")
    public void handle(NotifyMessage message) {
        log.info(">>>> [护理通知] 护理记录ID: {}, 客户ID: {}, 内容: {}",
                message.getBizId(), message.getCustomerId(), message.getContent());
    }
}
