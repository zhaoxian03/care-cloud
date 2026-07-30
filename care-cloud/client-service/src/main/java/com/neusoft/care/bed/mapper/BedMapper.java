package com.neusoft.care.bed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.bed.entity.Bed;
import org.apache.ibatis.annotations.Mapper;

/**
 * 床位Mapper接口 —— 访问bed表
 *
 * 核心逻辑：
 * 1. 继承MyBatis-Plus的BaseMapper，自动提供基本的CRUD操作（增删改查）
 * 2. 无自定义SQL方法，所有数据库操作均通过MyBatis-Plus的LambdaQueryWrapper/LambdaUpdateWrapper条件构造器实现
 *
 * @author CareCenter Team
 */
@Mapper
public interface BedMapper extends BaseMapper<Bed> {
}