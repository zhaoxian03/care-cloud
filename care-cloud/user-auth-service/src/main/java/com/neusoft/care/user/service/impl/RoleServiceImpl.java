package com.neusoft.care.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.user.entity.Role;
import com.neusoft.care.user.entity.RolePermission;
import com.neusoft.care.user.mapper.RoleMapper;
import com.neusoft.care.user.mapper.RolePermissionMapper;
import com.neusoft.care.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类 - 实现角色管理的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 角色CRUD操作，支持分页和关键词模糊搜索
 * 2. 创建/更新时校验角色编码（code）全局唯一性
 * 3. 系统预置角色（isSystem=1）受保护：不可修改编码，不可删除
 * 4. 删除角色时级联删除角色-权限关联记录
 *
 * 注意事项：创建、更新、状态变更、删除操作均使用 @Transactional 保证原子性
 *
 * @author CareCenter Team
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 查询所有启用的角色列表
     *
     * 核心逻辑：查询isDisabled=0的所有角色
     *
     * @return 启用状态的角色列表
     */
    @Override
    public List<Role> listAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getIsDisabled, 0);
        return roleMapper.selectList(wrapper);
    }

    /**
     * 分页查询角色，支持关键词搜索
     *
     * 核心逻辑：按关键词模糊匹配角色名称（name）或编码（code），结果按ID升序
     *
     * @param page 页码
     * @param size 每页条数
     * @param keyword 搜索关键词（匹配角色名称或编码，可为空）
     * @return MyBatis-Plus分页结果
     */
    @Override
    public IPage<Role> pageRoles(Integer page, Integer size, String keyword) {
        Page<Role> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Role::getName, keyword).or().like(Role::getCode, keyword));
        }
        wrapper.orderByAsc(Role::getId);
        return roleMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 创建角色
     *
     * 核心逻辑：
     * 1. 校验角色编码（code）全局唯一性
     * 2. 插入角色记录
     *
     * @param role 角色实体（name、code必填）
     * @return 创建后的角色（含自增ID）
     * @throws RuntimeException 角色编码已存在时抛出
     */
    @Override
    @Transactional
    public Role createRole(Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("角色编码已存在");
        }
        roleMapper.insert(role);
        return role;
    }

    /**
     * 更新角色
     *
     * 核心逻辑：
     * 1. 校验角色是否存在
     * 2. 系统预置角色保护：不允许修改编码（code）和系统标记（isSystem）
     * 3. 若修改编码，校验编码唯一性（排除自身）
     * 4. 更新后再查询返回最新数据
     *
     * @param role 角色实体（id必填）
     * @return 更新后的角色（含最新数据）
     * @throws RuntimeException 角色不存在或编码重复时抛出
     */
    @Override
    @Transactional
    public Role updateRole(Role role) {
        Role existing = roleMapper.selectById(role.getId());
        if (existing == null) {
            throw new RuntimeException("角色不存在");
        }
        if (existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            role.setCode(null);
            role.setIsSystem(null);
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode());
        wrapper.ne(Role::getId, role.getId());
        if (role.getCode() != null && roleMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("角色编码已存在");
        }
        roleMapper.updateById(role);
        return roleMapper.selectById(role.getId());
    }

    /**
     * 更新角色启用/禁用状态
     *
     * @param id 角色ID
     * @param isDisabled 状态值（0-启用，1-禁用）
     * @throws RuntimeException 角色不存在时抛出
     */
    @Override
    @Transactional
    public void updateStatus(Long id, Integer isDisabled) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setIsDisabled(isDisabled);
        roleMapper.updateById(role);
    }

    /**
     * 删除角色
     *
     * 核心逻辑：
     * 1. 校验角色是否存在
     * 2. 系统预置角色保护：不允许删除
     * 3. 先删除该角色的所有权限关联记录（级联删除），再删除角色本身
     *
     * @param id 角色ID
     * @throws RuntimeException 角色不存在或为系统预置角色时抛出
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (role.getIsSystem() != null && role.getIsSystem() == 1) {
            throw new RuntimeException("系统预置角色不可删除");
        }
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(rpWrapper);
        roleMapper.deleteById(id);
    }
}
