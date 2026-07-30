package com.neusoft.care.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.user.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper接口 - 访问 permission 表
 *
 * 核心逻辑：
 * 1. 继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 2. 自定义SQL：selectPermissionsByAdminId —— 通过admin_role和role_permission多表联查，
 *    查询某个管理员拥有的所有权限（用于RBAC鉴权）
 *
 * 注意事项：自定义SQL实现在对应的PermissionMapper.xml中
 *
 * @author CareCenter Team
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据管理员ID查询其拥有的权限列表
     *
     * 核心逻辑：通过 admin_role 表关联查询该管理员的所有角色，再通过 role_permission 表获取权限
     *
     * SQL逻辑：SELECT DISTINCT p.* FROM permission p
     *          INNER JOIN role_permission rp ON p.id = rp.permission_id
     *          INNER JOIN admin_role ar ON rp.role_id = ar.role_id
     *          WHERE ar.admin_id = #{adminId}
     *
     * @param adminId 管理员ID
     * @return 去重后的权限列表
     */
    List<Permission> selectPermissionsByAdminId(@Param("adminId") Long adminId);
}
