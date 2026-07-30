package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.ServiceCatalog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务产品目录 Mapper，MyBatis-Plus 自动提供 CRUD + 逻辑删除能力
 */
@Mapper
public interface ServiceCatalogMapper extends BaseMapper<ServiceCatalog> {
}
