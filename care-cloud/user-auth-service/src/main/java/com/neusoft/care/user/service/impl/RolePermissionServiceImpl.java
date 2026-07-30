package com.neusoft.care.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.user.entity.RolePermission;
import com.neusoft.care.user.mapper.RolePermissionMapper;
import com.neusoft.care.user.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色-权限关联服务实现类 - 实现角色权限分配的RBAC业务逻辑
 *
 * 核心逻辑：
 * 1. 查询角色关联的权限ID列表
 * 2. 保存角色权限：先删后增，实现全量覆盖式更新
 *
 * 注意事项：saveRolePermissions方法使用 @Transactional 保证删除+插入的原子性
 *
 * @author CareCenter Team
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 根据角色ID查询关联的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表（可能为空列表）
     */
    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        return rolePermissionMapper.selectList(wrapper).stream()
                .map(RolePermission::getPermissionId).collect(Collectors.toList());
    }

    /**
     * 保存角色权限关联（先删后增，全量替换）
     *
     * 核心逻辑：
     * 1. 删除该角色所有已有权限关联
     * 2. 逐条插入新的权限关联记录
     *
     * @param roleId 角色ID
     * @param permissionIds 新的权限ID列表（可为空，表示清空所有权限）
     */
    @Override
    @Transactional
    public void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rolePermissionMapper.insert(rp);
            }
        }
    }
}
