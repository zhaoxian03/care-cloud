package com.neusoft.care.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.user.entity.Role;
import com.neusoft.care.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器 - 处理角色管理相关的所有HTTP请求
 *
 * 核心逻辑：
 * 1. 角色列表查询（全部/分页）
 * 2. 角色CRUD操作
 * 3. 角色状态变更（启用/禁用）
 *
 * 权限控制：各接口通过@SaCheckPermission进行RBAC权限校验
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 查询所有启用的角色列表
     *
     * URL: GET /api/roles/list
     * 权限: role:view
     *
     * @return 启用状态的角色列表（用于下拉选择等场景）
     */
    @SaCheckPermission("role:view")
    @GetMapping("/list")
    public Result<List<Role>> listAll() {
        return Result.success(roleService.listAll());
    }

    /**
     * 分页查询角色
     *
     * URL: GET /api/roles/page
     * 权限: role:view
     *
     * @param page 页码（默认1）
     * @param size 每页条数（默认10）
     * @param keyword 搜索关键词（可选，匹配角色名称或编码）
     * @return 分页结果
     */
    @SaCheckPermission("role:view")
    @GetMapping("/page")
    public Result<PageResult<Role>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Role> result = roleService.pageRoles(page, size, keyword);
        PageResult<Role> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return Result.success(pageResult);
    }

    /**
     * 创建角色
     *
     * URL: POST /api/roles
     * 权限: role:create
     *
     * @param role 角色实体（name、code必填）
     * @return 创建后的角色（含自增ID）
     * @throws RuntimeException 角色编码已存在时抛出
     */
    @SaCheckPermission("role:create")
    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        return Result.success(roleService.createRole(role));
    }

    /**
     * 更新角色
     *
     * URL: PUT /api/roles/{id}
     * 权限: role:edit
     *
     * @param id 角色ID
     * @param role 角色实体（需更新的字段）
     * @return 更新后的角色
     * @throws RuntimeException 角色不存在或编码重复时抛出
     */
    @SaCheckPermission("role:edit")
    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return Result.success(roleService.updateRole(role));
    }

    /**
     * 更新角色状态
     *
     * URL: PUT /api/roles/{id}/status
     * 权限: role:edit
     *
     * @param id 角色ID
     * @param isDisabled 状态值（0-启用，1-禁用）
     * @return 操作结果
     * @throws RuntimeException 角色不存在时抛出
     */
    @SaCheckPermission("role:edit")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer isDisabled) {
        roleService.updateStatus(id, isDisabled);
        return Result.success();
    }

    /**
     * 删除角色
     *
     * URL: DELETE /api/roles/{id}
     * 权限: role:delete
     *
     * @param id 角色ID
     * @return 操作结果
     * @throws RuntimeException 角色不存在或为系统预置角色时抛出
     */
    @SaCheckPermission("role:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }
}
