package com.neusoft.care.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.user.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色权限控制器 - 处理角色权限分配相关的HTTP请求
 *
 * 核心逻辑：
 * 1. 查询角色当前关联的权限ID列表
 * 2. 批量分配权限给角色（先删后增，全量替换）
 *
 * 权限控制：所有接口需要 role:assign 权限
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/roles")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 查询角色的权限ID列表
     *
     * URL: GET /api/roles/{roleId}/permissions
     * 权限: role:assign
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @SaCheckPermission("role:assign")
    @GetMapping("/{roleId}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long roleId) {
        return Result.success(rolePermissionService.getPermissionIdsByRoleId(roleId));
    }

    /**
     * 批量分配权限给角色
     *
     * URL: POST /api/roles/{roleId}/permissions
     * 权限: role:assign
     *
     * 核心逻辑：先删除角色所有已有权限关联，再逐条插入新的权限关联（全量替换）
     *
     * @param roleId 角色ID
     * @param permissionIds 新的权限ID列表
     * @return 操作结果
     */
    @SaCheckPermission("role:assign")
    @PostMapping("/{roleId}/permissions")
    public Result<Void> savePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        rolePermissionService.saveRolePermissions(roleId, permissionIds);
        return Result.success();
    }
}
