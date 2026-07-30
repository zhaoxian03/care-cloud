package com.neusoft.care.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.common.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户Mapper接口 - 访问 customer 表
 * 
 * 功能说明：继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 
 * @author CareCenter Team
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
