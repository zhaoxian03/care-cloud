package com.neusoft.care.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.user.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色-权限关联Mapper接口 - 访问 role_permission 表
 *
 * 核心逻辑：继承MyBatis-Plus的BaseMapper，自动获得CRUD方法（insert、deleteById、updateById、selectList等）
 *
 * 注意事项：由于无自定义SQL，所有查询均通过ServiceImpl中的LambdaQueryWrapper构建条件
 *
 * @author CareCenter Team
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
