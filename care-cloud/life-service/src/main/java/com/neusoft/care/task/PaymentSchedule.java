package com.neusoft.care.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.entity.PaymentOrder;
import com.neusoft.care.mapper.CustomerSubscriptionMapper;
import com.neusoft.care.mapper.PaymentOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 支付订单定时任务 —— 每5分钟自动检查并处理过期未支付的订单
 *
 * 核心逻辑：
 * 1. 每隔5分钟扫描一次支付订单表，找出状态为 PENDING 且创建超过30分钟的订单
 * 2. 将过期订单状态更新为 EXPIRED（已过期）
 * 3. 如果过期订单关联的是订阅业务（SUBSCRIPTION），同步将对应订阅状态改为 CANCELLED（已取消）
 *
 * 注意事项：定时任务使用 fixedDelay=300000（5分钟），避免与支付回调产生并发冲突
 *
 * @author CareCenter Team
 */
@Component
@EnableScheduling
public class PaymentSchedule {

    @Autowired
    private PaymentOrderMapper paymentOrderMapper;

    @Autowired
    private CustomerSubscriptionMapper subscriptionMapper;

    /**
     * 过期未支付订单处理
     * 每5分钟执行一次：扫描创建超过30分钟仍为PENDING状态的支付订单，
     * 将其标记为EXPIRED，同时将关联的待支付订阅标记为CANCELLED
     * 定时任务注解
     */
    @Scheduled(fixedDelay = 300000)
    public void expirePendingOrders() {
        //查询过期订单
        LocalTime threshold = LocalTime.now().minusMinutes(30);
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentOrder::getStatus, "PENDING")
               .le(PaymentOrder::getCreateTime, threshold);
        List<PaymentOrder> orders = paymentOrderMapper.selectList(wrapper);

        //更新订单状态 + 关联订阅取消
        for (PaymentOrder order : orders) {
            order.setStatus("EXPIRED");
            paymentOrderMapper.updateById(order);

            if ("SUBSCRIPTION".equals(order.getBizType()) && order.getBizId() != null) {
                CustomerSubscription sub = subscriptionMapper.selectById(order.getBizId());
                if (sub != null && "PENDING".equals(sub.getStatus())) {
                    sub.setStatus("CANCELLED");
                    subscriptionMapper.updateById(sub);
                }
            }
        }
    }
}
