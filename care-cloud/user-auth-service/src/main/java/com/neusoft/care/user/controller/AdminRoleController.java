package com.neusoft.care.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.user.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员角色控制器 - 处理管理员RBAC角色分配相关的HTTP请求
 *
 * 核心逻辑：
 * 1. 查询管理员当前关联的角色ID列表
 * 2. 批量分配角色给管理员（先删后增，全量替换）
 *
 * 权限控制：所有接口需要 admin:edit 权限
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/admin")
public class AdminRoleController {

    @Autowired
    private AdminRoleService adminRoleService;

    /**
     * 查询管理员的角色ID列表
     *
     * URL: GET /api/admin/{adminId}/roles
     * 权限: admin:edit
     *
     * @param adminId 管理员ID
     * @return 角色ID列表
     */
    @SaCheckPermission("admin:edit")
    @GetMapping("/{adminId}/roles")
    public Result<List<Long>> getRoles(@PathVariable Long adminId) {
        return Result.success(adminRoleService.getRoleIdsByAdminId(adminId));
    }

    /**
     * 批量分配角色给管理员
     *
     * URL: POST /api/admin/{adminId}/roles
     * 权限: admin:edit
     *
     * 核心逻辑：先删除管理员所有已有角色关联，再逐条插入新的角色关联（全量替换）
     *
     * @param adminId 管理员ID
     * @param roleIds 新的角色ID列表
     * @return 操作结果
     */
    @SaCheckPermission("admin:edit")
    @PostMapping("/{adminId}/roles")
    public Result<Void> saveRoles(@PathVariable Long adminId, @RequestBody List<Long> roleIds) {
        adminRoleService.saveAdminRoles(adminId, roleIds);
        return Result.success();
    }
}
