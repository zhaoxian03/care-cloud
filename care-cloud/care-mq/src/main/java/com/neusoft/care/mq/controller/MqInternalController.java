package com.neusoft.care.mq.controller;

import com.neusoft.care.common.common.Result;
import com.neusoft.care.mq.dto.NotifyMessage;
import com.neusoft.care.mq.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息队列内部控制器
 *
 * 核心逻辑：
 * 1. 接收其他微服务通过 Feign 发来的 HTTP 请求
 * 2. 调用 MessageProducer 将消息投递到 RabbitMQ 对应的交换机
 * 3. 返回投递结果（同步确认投递成功）
 *
 * 注意事项：
 * - 此控制器不对外开放，仅被内部服务通过 Feign 调用
 * - 消息体为 NotifyMessage JSON，由 Feign 自动序列化
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/mq/internal")
public class MqInternalController {

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 发送支付成功通知
     */
    @PostMapping("/payment")
    public Result<Void> sendPaymentNotify(@RequestBody NotifyMessage message) {
        messageProducer.send("care.payment", "pay.success", message);
        return Result.success();
    }

    /**
     * 发送新入住通知
     */
    @PostMapping("/checkin")
    public Result<Void> sendCheckInNotify(@RequestBody NotifyMessage message) {
        messageProducer.send("care.checkin", "checkin.new", message);
        return Result.success();
    }

    /**
     * 发送护理完成通知
     */
    @PostMapping("/care")
    public Result<Void> sendCareNotify(@RequestBody NotifyMessage message) {
        messageProducer.send("care.record", "care.complete", message);
        return Result.success();
    }
}
