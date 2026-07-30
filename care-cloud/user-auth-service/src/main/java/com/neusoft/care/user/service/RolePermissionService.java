package com.neusoft.care.user.service;

import java.util.List;

/**
 * 角色-权限关联服务接口 - 定义角色权限分配的RBAC业务方法
 *
 * 核心逻辑：
 * 1. 查询角色关联的权限ID列表
 * 2. 保存角色权限：先删后增，实现全量替换
 *
 * 注意事项：批量分配权限使用先删后增策略，确保覆盖最新配置
 *
 * @author CareCenter Team
 */
public interface RolePermissionService {

    /**
     * 根据角色ID查询关联的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
     * 保存角色权限关联（先删后增）
     *
     * 核心逻辑：先删除该角色所有已有权限关联，再逐条插入新的权限关联
     *
     * @param roleId 角色ID
     * @param permissionIds 新的权限ID列表
     */
    void saveRolePermissions(Long roleId, List<Long> permissionIds);
}
