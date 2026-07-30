package com.neusoft.care.mq.dto;

import java.io.Serializable;

/**
 * 通用通知消息实体
 *
 * 所有业务消息的载体，通过 JSON 序列化后经由 RabbitMQ 投递。
 * 消费者反序列化后根据 bizType 判断消息来源，执行对应的异步处理逻辑。
 *
 * 注意事项：
 * 1. 实现 Serializable 以支持 RabbitMQ 的消息持久化
 * 2. bizType 与各消费者的 @RabbitListener 路由键一一对应
 * 3. customerId 为 0 时表示系统级通知
 *
 * @author CareCenter Team
 */
public class NotifyMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 业务类型：pay.success / checkin.new / care.complete */
    private String bizType;
    /** 关联客户 ID（系统通知时为 0） */
    private Long customerId;
    /** 通知标题 */
    private String title;
    /** 通知正文 */
    private String content;
    /** 业务记录 ID（订单号/入住记录ID/护理记录ID） */
    private Long bizId;
    /** 消息产生时间戳 */
    private Long timestamp;

    public NotifyMessage() {}

    public NotifyMessage(String bizType, Long customerId, String title, String content, Long bizId) {
        this.bizType = bizType;
        this.customerId = customerId;
        this.title = title;
        this.content = content;
        this.bizId = bizId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
