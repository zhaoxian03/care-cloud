package com.neusoft.care.user.service;

import com.neusoft.care.user.entity.Permission;

import java.util.List;

/**
 * 权限服务接口 - 定义权限管理的业务方法
 *
 * 核心逻辑：
 * 1. 权限CRUD + 树形结构构建
 * 2. 递归构建父子层级菜单树
 * 3. 根据管理员ID查询其RBAC权限列表
 *
 * 注意事项：删除权限前需校验无子权限存在；菜单树过滤仅取MENU类型节点
 *
 * @author CareCenter Team
 */
public interface PermissionService {

    /**
     * 获取完整权限树（所有类型）
     *
     * @return 树形权限列表，按sort字段升序排列
     */
    List<Permission> getPermissionTree();

    /**
     * 根据管理员ID查询其拥有的权限列表
     *
     * 注意：通过admin_role和role_permission多表联查
     *
     * @param adminId 管理员ID
     * @return 该管理员拥有的权限列表
     */
    List<Permission> getPermissionsByAdminId(Long adminId);

    /**
     * 构建菜单树
     *
     * 核心逻辑：过滤出MENU类型的权限，再递归构建树形结构
     *
     * @param permissions 权限列表（需包含children字段填充后的树形数据）
     * @return 菜单树列表
     */
    List<Permission> buildMenuTree(List<Permission> permissions);

    /**
     * 创建权限
     *
     * @param permission 权限实体
     * @return 创建后的权限（含自增ID）
     * @throws RuntimeException 权限编码已存在时抛出
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param permission 权限实体
     * @return 更新后的权限
     * @throws RuntimeException 权限不存在或编码重复时抛出
     */
    Permission updatePermission(Permission permission);

    /**
     * 删除权限
     *
     * 注意：存在子权限时不可删除
     *
     * @param id 权限ID
     * @throws RuntimeException 该权限下有子权限时抛出
     */
    void deletePermission(Long id);
}
