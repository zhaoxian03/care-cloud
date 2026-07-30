package com.neusoft.care.user.controller;

import com.neusoft.care.common.common.Result;
import com.neusoft.care.common.dto.LoginDTO;
import com.neusoft.care.common.dto.RegisterDTO;
import com.neusoft.care.user.dto.ChangePasswordDTO;
import com.neusoft.care.user.dto.UpdateProfileDTO;
import com.neusoft.care.user.service.AuthService;
import com.neusoft.care.user.vo.CustomerLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 客户认证控制器 - 处理客户端注册、登录及个人信息维护的HTTP请求
 *
 * 核心逻辑：
 * 1. 注册：手机号唯一校验 + BCrypt密码加密
 * 2. 登录：BCrypt验证 → SaToken签发Token
 * 3. 修改资料/密码：基于SaToken获取当前登录客户身份
 *
 * 注意事项：注册和登录为公开接口（无需权限认证），修改资料和密码需要登录态
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 客户注册接口
     *
     * URL: POST /api/auth/register
     * 权限: 无需认证（公开接口）
     *
     * @param dto 注册信息（手机号、密码、姓名、年龄、性别）
     * @return 操作结果
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }

    /**
     * 客户登录接口
     *
     * URL: POST /api/auth/login
     * 权限: 无需认证（公开接口）
     *
     * 核心逻辑：BCrypt验证 → SaToken登录 → 返回Token
     *
     * @param dto 登录信息（手机号 + 密码）
     * @return 客户登录信息VO（含Token）
     */
    @PostMapping("/login")
    public Result<CustomerLoginVO> login(@Valid @RequestBody LoginDTO dto) {
        CustomerLoginVO vo = authService.login(dto);
        return Result.success(vo);
    }

    /**
     * 修改个人资料接口
     *
     * URL: PUT /api/auth/profile
     * 权限: 需要登录态（customer）
     *
     * @param dto 新的个人资料信息（字段可选）
     * @return 操作结果
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        authService.updateProfile(dto);
        return Result.success();
    }

    /**
     * 修改密码接口
     *
     * URL: PUT /api/auth/password
     * 权限: 需要登录态（customer）
     *
     * @param dto 新旧密码
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        authService.changePassword(dto);
        return Result.success();
    }
}
