package com.neusoft.care.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.common.dto.AdminLoginDTO;
import com.neusoft.care.user.dto.CreateAdminDTO;
import com.neusoft.care.user.vo.AdminLoginVO;
import com.neusoft.care.user.vo.AdminVO;
import com.neusoft.care.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员控制器 - 处理管理员相关的所有HTTP请求
 * 
 * 功能说明：
 * 1. 管理员登录：验证账号和密码，返回JWT Token
 * 2. 管理员退出：将当前Token加入Redis黑名单
 * 3. 获取当前管理员信息
 * 4. 管理员管理：创建、查询、修改状态、删除（仅超级管理员）
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录接口
     * URL: POST /api/admin/login
     * 权限: 无需认证（公开接口）
     * RequestBody负责将前端传来的JSON数据转换为AdminLoginDTO对象，
     * Valid注解用于启用Spring的验证机制，确保传入的数据符合AdminLoginDTO中定义的约束条件。
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        AdminLoginVO vo = adminService.login(dto);
        return Result.success(vo);
    }

    /**
     * 管理员退出登录接口
     * URL: POST /api/admin/logout
     * 权限: 需要认证
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        adminService.logout();
        return Result.success();
    }

    /**
     * 获取当前登录管理员信息接口
     * URL: GET /api/admin/current
     * 权限: 需要认证
     */
    @GetMapping("/current")
    public Result<AdminVO> getCurrentAdmin() {
        AdminVO vo = adminService.getCurrentAdmin();
        return Result.success(vo);
    }

    /**
     * 刷新Token接口
     * URL: POST /api/admin/refresh
     * 权限: 需要认证
     */
    @PostMapping("/refresh")
    public Result<String> refresh() {
        String token = adminService.refreshToken();
        return Result.success(token);
    }

    /**
     * 创建管理员接口（仅超级管理员可操作）
     * URL: POST /api/admin
     * 权限: 需要超级管理员权限
     */
    @SaCheckPermission("admin:create")
    @PostMapping
    public Result<Void> createAdmin(@Valid @RequestBody CreateAdminDTO dto) {
        Long creatorId = StpUtil.getLoginIdAsLong();
        adminService.createAdmin(dto, creatorId);
        return Result.success();
    }

    /**
     * 创建健康管家接口（超级管理员和普通管理员均可操作）
     * URL: POST /api/admin/caregiver
     * 权限: 需要认证（管理员及以上）
     */
    @SaCheckPermission("caregiver:create")
    @PostMapping("/caregiver")
    public Result<Void> createCaregiver(@Valid @RequestBody CreateAdminDTO dto) {
        Long creatorId = StpUtil.getLoginIdAsLong();
        adminService.createCaregiver(dto, creatorId);
        return Result.success();
    }

    /**
     * 管理员分页查询接口
     * URL: GET /api/admin/page
     * 权限: 需要超级管理员权限
     */
    @SaCheckPermission("admin:view")
    @GetMapping("/page")
    public Result<PageResult<AdminVO>> pageAdmins(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        PageResult<AdminVO> result = adminService.pageAdmins(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 更新管理员状态接口
     * URL: PUT /api/admin/{id}/status
     * 权限: 需要超级管理员权限
     */
    @SaCheckPermission("admin:status")
    @PutMapping("/{id}/status")
    public Result<Void> updateAdminStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateAdminStatus(id, status);
        return Result.success();
    }

    /**
     * 删除管理员接口
     * URL: DELETE /api/admin/{id}
     * 权限: 需要超级管理员权限
     */
    @SaCheckPermission("admin:delete")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        Long currentAdminId = StpUtil.getLoginIdAsLong();
        adminService.deleteAdmin(id, currentAdminId);
        return Result.success();
    }
}
