package com.neusoft.care.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.user.entity.Permission;
import com.neusoft.care.user.mapper.PermissionMapper;
import com.neusoft.care.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现类 - 实现权限管理及菜单树构建的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 权限CRUD操作，支持编码唯一性校验
 * 2. 树形结构构建：递归算法根据parentId构建父子层级
 * 3. 菜单树构建：从权限列表中过滤MENU类型，再递归构建树
 * 4. 根据管理员ID查询权限：通过admin_role → role_permission → permission多表联查
 *
 * 注意事项：删除权限前需校验无子权限存在；树构建方法为私有递归方法
 *
 * @author CareCenter Team
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 获取完整权限树（含所有类型的权限）
     *
     * 核心逻辑：查询所有权限记录（按sort升序），递归构建树形结构
     *
     * @return 树形权限列表，根节点为parentId=null的权限
     */
    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> all = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getSort));
        return buildTree(all, null);
    }

    /**
     * 根据管理员ID查询其拥有的权限列表
     *
     * 核心逻辑：调用PermissionMapper中的自定义SQL，通过admin_role和role_permission表联查
     *
     * @param adminId 管理员ID
     * @return 该管理员拥有的权限列表
     */
    @Override
    public List<Permission> getPermissionsByAdminId(Long adminId) {
        return permissionMapper.selectPermissionsByAdminId(adminId);
    }

    /**
     * 构建菜单树
     *
     * 核心逻辑：
     * 1. 从权限列表中筛选type为"MENU"的记录
     * 2. 递归构建树形结构
     *
     * @param permissions 权限列表
     * @return 菜单树列表（仅含MENU类型节点）
     */
    @Override
    public List<Permission> buildMenuTree(List<Permission> permissions) {
        List<Permission> menus = new ArrayList<>();
        for (Permission p : permissions) {
            if ("MENU".equals(p.getType())){
                menus.add(p);
            }
        }
        return buildTree(menus, null);
    }

    /**
     * 递归构建树形结构
     *
     * 核心逻辑：
     * 1. 遍历所有权限，筛选parentId匹配的节点
     * 2. 对匹配的节点递归构建其子树
     * 3. 根节点条件：parentId为null
     *
     * @param all 所有权限列表
     * @param parentId 父节点ID（null表示根节点）
     * @return 树形权限列表
     */
    private List<Permission> buildTree(List<Permission> all, Long parentId) {
        List<Permission> result = new ArrayList<>();
        for (Permission p : all) {
            boolean match = (parentId == null && p.getParentId() == null)
                    || (parentId != null && parentId.equals(p.getParentId()));
            if (match) {
                p.setChildren(buildTree(all, p.getId()));
                result.add(p);
            }
        }
        return result;
    }

    /**
     * 创建权限
     *
     * 核心逻辑：校验权限编码（code）全局唯一性后插入
     *
     * @param permission 权限实体
     * @return 创建后的权限（含自增ID）
     * @throws RuntimeException 权限编码已存在时抛出
     */
    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, permission.getCode());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("权限编码已存在");
        }
        permissionMapper.insert(permission);
        return permission;
    }

    /**
     * 更新权限
     *
     * 核心逻辑：
     * 1. 校验权限是否存在
     * 2. 若修改编码，校验编码唯一性（排除自身）
     * 3. 更新权限记录
     *
     * @param permission 权限实体（id必填）
     * @return 更新后的权限
     * @throws RuntimeException 权限不存在或编码重复时抛出
     */
    @Override
    @Transactional
    public Permission updatePermission(Permission permission) {
        Permission existing = permissionMapper.selectById(permission.getId());
        if (existing == null) {
            throw new RuntimeException("权限不存在");
        }
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, permission.getCode());
        wrapper.ne(Permission::getId, permission.getId());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("权限编码已存在");
        }
        permissionMapper.updateById(permission);
        return permission;
    }

    /**
     * 删除权限
     *
     * 核心逻辑：先校验是否有子权限存在，再执行物理删除
     *
     * @param id 权限ID
     * @throws RuntimeException 该权限下有子权限时抛出
     */
    @Override
    @Transactional
    public void deletePermission(Long id) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该权限下有子权限，无法删除");
        }
        permissionMapper.deleteById(id);
    }
}
