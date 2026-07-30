package com.neusoft.care.mq.consumer;

import com.neusoft.care.mq.dto.NotifyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 新入住消息消费者
 *
 * 核心逻辑：
 * 1. 监听 care.checkin.notify 队列，接收 checkin.new 路由消息
 * 2. 记录入住通知日志（后续扩展：通知护理人员、更新床位统计）
 *
 * @author CareCenter Team
 */
@Component
public class CheckInConsumer {
    private static final Logger log = LoggerFactory.getLogger(CheckInConsumer.class);

    @RabbitListener(queues = "care.checkin.notify")
    public void handle(NotifyMessage message) {
        log.info(">>>> [入住通知] 入住记录ID: {}, 客户ID: {}, 内容: {}",
                message.getBizId(), message.getCustomerId(), message.getContent());
    }
}
