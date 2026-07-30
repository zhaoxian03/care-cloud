package com.neusoft.care.controller.customer;

import cn.dev33.satoken.stp.StpUtil;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.AlipayService;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.dto.RenewSubscriptionDTO;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.entity.PaymentOrder;
import com.neusoft.care.mapper.CustomerSubscriptionMapper;
import com.neusoft.care.mapper.PaymentOrderMapper;
import com.neusoft.care.service.CustomerSubscriptionService;
import com.neusoft.care.common.feign.MqServiceFeignClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * C端支付控制器 —— 面向客户/长者App的支付宝支付接口
 *
 * 核心逻辑：
 * 1. 客户在前端发起支付请求，系统生成支付订单并调用支付宝API获取支付表单HTML
 * 2. 支付宝异步通知回调，验证签名后将支付订单状态更新为SUCCESS
 * 3. 支付成功后根据业务类型（SUBSCRIPTION/RENEW）自动激活订阅或执行续期
 *
 * 注意事项：支付回调接口需暴露给外网，签名验证环节涉及资金安全，不可跳过
 *
 * @author CareCenter Team
 */
@RestController
public class PaymentController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private PaymentOrderMapper paymentOrderMapper;

    @Autowired
    private CustomerSubscriptionService subscriptionService;

    @Autowired
    private CustomerSubscriptionMapper subscriptionMapper;

    @Autowired
    private MqServiceFeignClient mqServiceFeignClient;

    /**
     * 创建支付订单并返回支付宝支付表单HTML
     * 客户端拿到HTML后直接渲染即可调起支付宝支付
     *
     * @param req 支付请求（金额、商品描述、业务类型、业务ID、时长）
     * @return 支付宝支付表单HTML字符串
     */
    @PostMapping("/api/app/payment/create")
    public Result<String> create(@RequestBody PaymentRequest req) {
        Long customerId = StpUtil.getLoginIdAsLong();

        // 生成订单号：时间戳 + 7位随机数
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%07d", new Random().nextInt(10_000_000));
        BigDecimal amount = req.getTotalAmount() != null ? BigDecimal.valueOf(req.getTotalAmount()) : BigDecimal.valueOf(0.01);
        String subject = req.getSubject() != null ? req.getSubject() : "养老服务费";

        // 构建支付订单
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(customerId);
        order.setSubject(subject);
        order.setBizId(req.getBizId());
        order.setBizType(req.getBizType());
        order.setDuration(req.getDuration());
        order.setTotalAmount(amount);
        order.setStatus("PENDING");
        order.setCreateDate(LocalDate.now());
        order.setCreateTime(LocalTime.now());
        paymentOrderMapper.insert(order);

        // 调用支付宝SDK获取支付表单HTML
        try {
            String html = alipayService.pay(orderNo, String.format("%.2f", amount), subject);
            return Result.success(html);
        } catch (AlipayApiException e) {
            return Result.error(500, "支付请求失败: " + e.getMessage());
        }
    }

    /**
     * 支付宝支付异步通知回调
     * 验证签名后更新订单状态为SUCCESS，并根据业务类型激活订阅或执行续期
     *
     * @param request 支付宝POST的异步通知请求
     * @return "success" 表示已正确处理，"fail" 表示验签失败或参数缺失
     */
    @PostMapping("/api/payment/notify")
    public String notify(HttpServletRequest request) {
        // 1. 提取参数并验签
        Map<String, String> params = alipayService.extractParams(request);
        boolean ok = alipayService.verifySign(params);
        if (ok && params.containsKey("out_trade_no")) {
            String orderNo = params.get("out_trade_no");
            PaymentOrder order = paymentOrderMapper.selectOne(
                    new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
            if (order != null && !"SUCCESS".equals(order.getStatus())) {
                // 2.乐观锁：原子更新，WHERE status='PENDING' 确保并发回调只有一次生效
                LambdaUpdateWrapper<PaymentOrder> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PaymentOrder::getId, order.getId())
                        .eq(PaymentOrder::getStatus, "PENDING")
                        .set(PaymentOrder::getStatus, "SUCCESS")
                        .set(PaymentOrder::getPayTime, LocalTime.now());
                int rows = paymentOrderMapper.update(null, updateWrapper);
                if (rows == 0) {
                    // 并发回调已经被处理，直接返回成功避免重复通知
                    return "success";
                }

                // 3. 根据业务类型触发订阅激活或续期

                //新增
                if ("SUBSCRIPTION".equals(order.getBizType()) && order.getBizId() != null) {
                    CustomerSubscription sub = subscriptionMapper.selectById(order.getBizId());
                    if (sub != null && "PENDING".equals(sub.getStatus())) {
                        sub.setStatus("ACTIVE");
                        subscriptionMapper.updateById(sub);
                    }
                } else if ("RENEW".equals(order.getBizType()) && order.getBizId() != null) { //续约
                    RenewSubscriptionDTO dto = new RenewSubscriptionDTO();
                    dto.setDuration(order.getDuration());
                        subscriptionService.renew(order.getBizId(), dto);
                    }
                }

                //4. 异步通知 MQ：支付成功
                Map<String, Object> msg = new HashMap<>();
                msg.put("bizType", "pay.success");
                msg.put("customerId", order.getCustomerId());
                msg.put("title", "支付成功");
                msg.put("content", "订单 " + orderNo + " 支付成功，金额 " + order.getTotalAmount());
                msg.put("bizId", order.getId());
                msg.put("timestamp", System.currentTimeMillis());

            // ⑤ 发 MQ 异步通知（短信/推送/报表，后续扩展）
                mqServiceFeignClient.sendPaymentNotify(msg);
            return "success";
        }
        return "fail";
    }

    /**
     * 分页查询当前客户的支付订单列表
     *
     * @param page    页码
     * @param size    每页条数
     * @param status  订单状态筛选（PENDING/SUCCESS/EXPIRED，可选）
     * @param orderNo 订单号模糊搜索（可选）
     * @return 分页结果
     */
    @GetMapping("/api/app/payment/list")
    public Result<PageResult<PaymentOrder>> list(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String orderNo) {
        Long customerId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentOrder::getCustomerId, customerId);

        if (status != null && !status.isEmpty()) wrapper.eq(PaymentOrder::getStatus, status);
        if (orderNo != null && !orderNo.isEmpty()) wrapper.like(PaymentOrder::getOrderNo, orderNo);

        wrapper.orderByDesc(PaymentOrder::getCreateDate, PaymentOrder::getCreateTime);
        IPage<PaymentOrder> p = paymentOrderMapper.selectPage(new Page<>(page, size), wrapper);

        PageResult<PaymentOrder> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        return Result.success(result);
    }

    @Data
    public static class PaymentRequest {
        private Double totalAmount;
        private String subject;
        private Long bizId;
        private String bizType;
        private Integer duration;
    }
}
