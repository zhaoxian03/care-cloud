package com.neusoft.care.user.service;

import java.util.List;

/**
 * 管理员-角色关联服务接口 - 定义管理员RBAC角色分配的业务方法
 *
 * 核心逻辑：
 * 1. 查询管理员关联的角色ID列表
 * 2. 保存管理员角色：先删后增，实现全量替换
 *
 * 注意事项：批量分配角色使用先删后增策略
 *
 * @author CareCenter Team
 */
public interface AdminRoleService {

    /**
     * 根据管理员ID查询关联的角色ID列表
     *
     * @param adminId 管理员ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByAdminId(Long adminId);

    /**
     * 保存管理员角色关联（先删后增）
     *
     * 核心逻辑：先删除该管理员所有已有角色关联，再逐条插入新的角色关联
     *
     * @param adminId 管理员ID
     * @param roleIds 新的角色ID列表
     */
    void saveAdminRoles(Long adminId, List<Long> roleIds);
}
