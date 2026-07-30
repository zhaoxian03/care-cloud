package com.neusoft.care.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.user.entity.AdminRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员-角色关联Mapper接口 - 访问 admin_role 表
 *
 * 核心逻辑：继承MyBatis-Plus的BaseMapper，自动获得CRUD方法，实现管理员与角色的多对多关联
 *
 * 注意事项：由于无自定义SQL，所有查询均通过ServiceImpl中的LambdaQueryWrapper构建条件
 *
 * @author CareCenter Team
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
}
