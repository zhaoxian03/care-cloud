package com.neusoft.care.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.user.entity.AdminRole;
import com.neusoft.care.user.mapper.AdminRoleMapper;
import com.neusoft.care.user.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员-角色关联服务实现类 - 实现管理员RBAC角色分配的业务逻辑
 *
 * 核心逻辑：
 * 1. 查询管理员关联的角色ID列表
 * 2. 保存管理员角色：先删后增，实现全量覆盖式更新
 *
 * 注意事项：saveAdminRoles方法使用 @Transactional 保证删除+插入的原子性
 *
 * @author CareCenter Team
 */
@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    /**
     * 根据管理员ID查询关联的角色ID列表
     *
     * @param adminId 管理员ID
     * @return 角色ID列表（可能为空列表）
     */
    @Override
    public List<Long> getRoleIdsByAdminId(Long adminId) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        return adminRoleMapper.selectList(wrapper).stream()
                .map(AdminRole::getRoleId).collect(Collectors.toList());
    }

    /**
     * 保存管理员角色关联（先删后增，全量替换）
     *
     * 核心逻辑：
     * 1. 删除该管理员所有已有角色关联
     * 2. 逐条插入新的角色关联记录
     *
     * @param adminId 管理员ID
     * @param roleIds 新的角色ID列表（可为空，表示清空所有角色）
     */
    @Override
    @Transactional
    public void saveAdminRoles(Long adminId, List<Long> roleIds) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        adminRoleMapper.delete(wrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                AdminRole ar = new AdminRole();
                ar.setAdminId(adminId);
                ar.setRoleId(roleId);
                adminRoleMapper.insert(ar);
            }
        }
    }
}
