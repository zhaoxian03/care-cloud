package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.dto.RenewSubscriptionDTO;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.entity.PaymentOrder;
import com.neusoft.care.mapper.CustomerSubscriptionMapper;
import com.neusoft.care.mapper.PaymentOrderMapper;
import com.neusoft.care.service.CustomerSubscriptionService;
import com.neusoft.care.service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付订单服务实现 —— 管理端支付订单查询与状态变更
 *
 * 核心逻辑：
 * 1. 分页查询支付订单，支持按状态、订单号、客户ID、日期范围多条件筛选
 * 2. 手动更新订单状态为SUCCESS时，同步激活订阅或执行续期（@Transactional 保证事务一致性）
 *
 * 注意事项：updatePaymentStatus 包含事务操作，订单状态更新、订阅激活/续期在同一事务中完成
 *
 * @author CareCenter Team
 */
@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {

    @Autowired
    private PaymentOrderMapper paymentOrderMapper;

    @Autowired
    private CustomerSubscriptionMapper subscriptionMapper;

    @Autowired
    private CustomerSubscriptionService subscriptionService;

    /**
     * 分页查询支付订单，支持多条件筛选
     *
     * @param page       页码
     * @param size       每页条数
     * @param status     订单状态筛选（PENDING/SUCCESS/EXPIRED，可选）
     * @param orderNo    订单号模糊搜索（可选）
     * @param customerId 客户ID筛选（可选）
     * @param startDate  开始日期筛选（可选）
     * @param endDate    结束日期筛选（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<PaymentOrder> pagePayments(int page, int size, String status, String orderNo,
                                                  Long customerId, String startDate, String endDate) {
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();

        //   动态添加查询条件
        if (status != null && !status.isEmpty()) wrapper.eq(PaymentOrder::getStatus, status);
        if (orderNo != null && !orderNo.isEmpty()) wrapper.like(PaymentOrder::getOrderNo, orderNo);
        if (customerId != null) wrapper.eq(PaymentOrder::getCustomerId, customerId);
        if (startDate != null && !startDate.isEmpty()) wrapper.ge(PaymentOrder::getCreateDate, startDate);
        if (endDate != null && !endDate.isEmpty()) wrapper.le(PaymentOrder::getCreateDate, endDate);

        // 按创建时间倒序
        wrapper.orderByDesc(PaymentOrder::getCreateDate, PaymentOrder::getCreateTime);
        com.baomidou.mybatisplus.core.metadata.IPage<PaymentOrder> p = paymentOrderMapper.selectPage(new Page<>(page, size), wrapper);
        PageResult<PaymentOrder> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        return result;
    }

    /**
     * 手动更新支付订单状态
     * 当状态更新为SUCCESS时，自动根据业务类型（SUBSCRIPTION/RENEW）触发订阅激活或续期
     * 使用 @Transactional 保证订单状态与订阅状态的一致性
     *
     * @param id     支付订单ID
     * @param status 目标状态（SUCCESS/EXPIRED等）
     * @throws RuntimeException 订单不存在
     */
    @Override
    @Transactional
    public void updatePaymentStatus(Long id, String status) {
        PaymentOrder order = paymentOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);

        // 若状态更新为 SUCCESS，触发订阅激活或续期
        if ("SUCCESS".equals(status)) {
            if ("SUBSCRIPTION".equals(order.getBizType()) && order.getBizId() != null) {
                // 场景1：新购订阅 → 将状态从 PENDING 改为 ACTIVE
                CustomerSubscription sub = subscriptionMapper.selectById(order.getBizId());
                if (sub != null && "PENDING".equals(sub.getStatus())) {
                    sub.setStatus("ACTIVE");
                    subscriptionMapper.updateById(sub);
                }
            } else if ("RENEW".equals(order.getBizType()) && order.getBizId() != null) {
                // 场景2：续期 → 调用续期服务，延长订阅到期日
                RenewSubscriptionDTO dto = new RenewSubscriptionDTO();
                dto.setDuration(order.getDuration());
                subscriptionService.renew(order.getBizId(), dto);
            }
        }

        paymentOrderMapper.updateById(order);
    }
}
