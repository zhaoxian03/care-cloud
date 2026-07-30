package com.neusoft.care.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.user.entity.Permission;
import com.neusoft.care.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器 - 处理权限管理相关的所有HTTP请求
 *
 * 核心逻辑：
 * 1. 权限树查询（全量权限树/菜单树）
 * 2. 权限CRUD操作
 *
 * 权限控制：各接口通过@SaCheckPermission进行RBAC权限校验
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限树
     *
     * URL: GET /api/permissions/tree
     * 权限: permission:view
     *
     * @return 树形权限列表（含所有类型）
     */
    @SaCheckPermission("permission:view")
    @GetMapping("/tree")
    public Result<List<Permission>> getTree() {
        return Result.success(permissionService.getPermissionTree());
    }

    /**
     * 获取全部权限树（与/tree接口返回相同数据）
     *
     * URL: GET /api/permissions/all
     * 权限: permission:view
     *
     * @return 树形权限列表
     */
    @SaCheckPermission("permission:view")
    @GetMapping("/all")
    public Result<List<Permission>> getAll() {
        return Result.success(permissionService.getPermissionTree());
    }

    /**
     * 创建权限
     *
     * URL: POST /api/permissions
     * 权限: permission:create
     *
     * @param permission 权限实体（name、code、type必填）
     * @return 创建后的权限（含自增ID）
     * @throws RuntimeException 权限编码已存在时抛出
     */
    @SaCheckPermission("permission:create")
    @PostMapping
    public Result<Permission> create(@RequestBody Permission permission) {
        return Result.success(permissionService.createPermission(permission));
    }

    /**
     * 更新权限
     *
     * URL: PUT /api/permissions/{id}
     * 权限: permission:edit
     *
     * @param id 权限ID
     * @param permission 权限实体（需更新的字段）
     * @return 更新后的权限
     * @throws RuntimeException 权限不存在或编码重复时抛出
     */
    @SaCheckPermission("permission:edit")
    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return Result.success(permissionService.updatePermission(permission));
    }

    /**
     * 删除权限
     *
     * URL: DELETE /api/permissions/{id}
     * 权限: permission:delete
     *
     * @param id 权限ID
     * @return 操作结果
     * @throws RuntimeException 该权限下有子权限时抛出
     */
    @SaCheckPermission("permission:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
}
