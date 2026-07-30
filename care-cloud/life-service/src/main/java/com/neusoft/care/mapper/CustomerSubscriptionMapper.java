package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.CustomerSubscription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户订阅记录 Mapper，MyBatis-Plus 自动提供 CRUD + 逻辑删除能力
 */
@Mapper
public interface CustomerSubscriptionMapper extends BaseMapper<CustomerSubscription> {
}
