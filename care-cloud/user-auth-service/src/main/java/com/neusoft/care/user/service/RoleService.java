package com.neusoft.care.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.user.entity.Role;

import java.util.List;

/**
 * 角色服务接口 - 定义角色管理的业务方法
 *
 * 核心逻辑：
 * 1. 角色CRUD操作
 * 2. 系统预置角色受保护（isSystem=1），不可修改编码和删除
 * 3. 角色编码全局唯一校验
 *
 * 注意事项：删除角色时需级联删除角色-权限关联记录
 *
 * @author CareCenter Team
 */
public interface RoleService {

    /**
     * 查询所有启用的角色列表
     *
     * @return 启用状态的角色列表
     */
    List<Role> listAll();

    /**
     * 分页查询角色，支持关键词搜索
     *
     * @param page 页码
     * @param size 每页条数
     * @param keyword 搜索关键词（匹配角色名称或编码）
     * @return 分页结果
     */
    IPage<Role> pageRoles(Integer page, Integer size, String keyword);

    /**
     * 创建角色
     *
     * @param role 角色实体
     * @return 创建后的角色（含自增ID）
     * @throws RuntimeException 角色编码已存在时抛出
     */
    Role createRole(Role role);

    /**
     * 更新角色
     *
     * 注意：系统预置角色不允许修改编码（code）和系统标记（isSystem）
     *
     * @param role 角色实体
     * @return 更新后的角色
     * @throws RuntimeException 角色不存在或编码重复时抛出
     */
    Role updateRole(Role role);

    /**
     * 更新角色启用/禁用状态
     *
     * @param id 角色ID
     * @param isDisabled 状态值（0-启用，1-禁用）
     * @throws RuntimeException 角色不存在时抛出
     */
    void updateStatus(Long id, Integer isDisabled);

    /**
     * 删除角色
     *
     * 注意：系统预置角色不可删除，同时会级联删除角色-权限关联记录
     *
     * @param id 角色ID
     * @throws RuntimeException 角色不存在或为系统预置角色时抛出
     */
    void deleteRole(Long id);
}
