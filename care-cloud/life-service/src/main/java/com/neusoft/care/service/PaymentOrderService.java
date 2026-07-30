package com.neusoft.care.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.entity.PaymentOrder;

/**
 * 支付订单服务接口 —— 定义支付订单的查询和状态更新方法
 *
 * @author CareCenter Team
 */
public interface PaymentOrderService {

    /**
     * 分页查询支付订单，支持多条件筛选
     *
     * @param page       页码
     * @param size       每页条数
     * @param status     订单状态（可选）
     * @param orderNo    订单号（可选，模糊搜索）
     * @param customerId 客户ID（可选）
     * @param startDate  开始日期（可选）
     * @param endDate    结束日期（可选）
     * @return 分页结果
     */
    PageResult<PaymentOrder> pagePayments(int page, int size, String status, String orderNo,
                                           Long customerId, String startDate, String endDate);

    /**
     * 更新支付订单状态
     * 状态为SUCCESS时会同步激活订阅或执行续期
     *
     * @param id     支付订单ID
     * @param status 目标状态
     */
    void updatePaymentStatus(Long id, String status);
}
