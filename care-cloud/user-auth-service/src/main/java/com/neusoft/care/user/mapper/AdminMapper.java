package com.neusoft.care.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.user.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员Mapper接口 - 访问 admin 表
 *
 * 核心逻辑：继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作（selectById、insert、updateById、deleteById等）
 *
 * 注意事项：由于无自定义SQL，所有业务查询均通过ServiceImpl中的LambdaQueryWrapper构建条件
 *
 * @author CareCenter Team
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
