package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 支付订单实体 —— 对应数据库 payment_order 表
 *
 * 核心逻辑：
 * 1. 记录每笔支付交易：订单号、客户ID、金额、业务类型（SUBSCRIPTION/RENEW）、状态
 * 2. 状态流转：PENDING -> SUCCESS（支付成功）/ EXPIRED（超时过期）
 * 3. 支付成功后根据 biz_type 触发订阅激活或续期
 *
 * 注意事项：order_no 为业务唯一标识，用于支付宝异步通知回查
 *
 * @author CareCenter Team
 */
@Data
@TableName("payment_order")
public class PaymentOrder {

    @TableId(type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("customer_id")
    private Long customerId;

    @TableField("subject")
    private String subject;

    @TableField("biz_id")
    private Long bizId;

    @TableField("biz_type")
    private String bizType;

    @TableField("duration")
    private Integer duration;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private String status;

    @TableField("create_date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createDate;

    @TableField("create_time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalTime createTime;

    @TableField("pay_time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalTime payTime;

    @TableLogic
    @TableField("is_deleted")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer isDeleted;
}
