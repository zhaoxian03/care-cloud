package com.neusoft.care.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 *
 * 核心逻辑：
 * 1. 声明 3 个业务交换机（Direct 模式，精确路由键匹配）
 * 2. 声明 3 个持久化队列
 * 3. 绑定交换机与队列，指定路由键
 * 4. 配置 RabbitTemplate 使用 JSON 序列化，替代 Spring 默认的 Java 序列化
 *
 * 交换机/队列/路由键映射：
 * care.payment  → care.payment.notify  → pay.success     （支付成功通知）
 * care.checkin  → care.checkin.notify  → checkin.new     （新入住通知）
 * care.record   → care.record.notify   → care.complete   （护理完成通知）
 *
 * 注意事项：
 * - 队列和消息均持久化（durable=true），RabbitMQ 重启不丢失
 * - 交换机使用 Direct 类型，不支持通配符路由
 * - 如需要延迟消息，需另外安装 rabbitmq_delayed_message_exchange 插件
 *
 * @author CareCenter Team
 */
@Configuration
public class RabbitMqConfig {

    // ==================== 交换机 ====================

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange("care.payment", true, false);
    }

    @Bean
    public DirectExchange checkinExchange() {
        return new DirectExchange("care.checkin", true, false);
    }

    @Bean
    public DirectExchange recordExchange() {
        return new DirectExchange("care.record", true, false);
    }

    // ==================== 队列 ====================

    @Bean
    public Queue paymentNotifyQueue() {
        return QueueBuilder.durable("care.payment.notify").build();
    }

    @Bean
    public Queue checkinNotifyQueue() {
        return QueueBuilder.durable("care.checkin.notify").build();
    }

    @Bean
    public Queue recordNotifyQueue() {
        return QueueBuilder.durable("care.record.notify").build();
    }

    // ==================== 绑定 ====================

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentNotifyQueue()).to(paymentExchange()).with("pay.success");
    }

    @Bean
    public Binding checkinBinding() {
        return BindingBuilder.bind(checkinNotifyQueue()).to(checkinExchange()).with("checkin.new");
    }

    @Bean
    public Binding recordBinding() {
        return BindingBuilder.bind(recordNotifyQueue()).to(recordExchange()).with("care.complete");
    }

    // ==================== RabbitTemplate 配置 ====================

    /**
     * JSON 消息转换器 Bean
     * Spring AMQP 会自动注入给 RabbitTemplate 和 @RabbitListener 容器，
     * 保证生产者和消费者使用相同的序列化方式。
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
