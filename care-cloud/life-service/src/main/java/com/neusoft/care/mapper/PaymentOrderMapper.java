package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单 Mapper —— MyBatis-Plus BaseMapper 提供基础CRUD，无额外自定义SQL
 *
 * @author CareCenter Team
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
}
