package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.ServiceCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务分类 Mapper，MyBatis-Plus 自动提供 CRUD + 逻辑删除能力
 */
@Mapper
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory> {
}
