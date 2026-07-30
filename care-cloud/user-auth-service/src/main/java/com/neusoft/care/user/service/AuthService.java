package com.neusoft.care.user.service;

import com.neusoft.care.common.dto.LoginDTO;
import com.neusoft.care.common.dto.RegisterDTO;
import com.neusoft.care.user.vo.CustomerLoginVO;

import com.neusoft.care.user.dto.ChangePasswordDTO;
import com.neusoft.care.user.dto.UpdateProfileDTO;

/**
 * 客户认证服务接口 - 定义客户端注册、登录及个人信息维护的业务方法
 *
 * 核心逻辑：
 * 1. 注册：手机号唯一校验 + BCrypt密码加密 + 插入Customer记录
 * 2. 登录：BCrypt密码验证 → SaToken签发Token → 返回客户信息+Token
 * 3. 修改资料/密码：从SaToken获取当前登录客户ID进行操作
 *
 * 注意事项：所有密码操作均使用BCrypt加密，登录状态由SaToken管理
 *
 * @author CareCenter Team
 */
public interface AuthService {

    /**
     * 客户注册
     *
     * @param dto 注册信息（含手机号、密码、姓名等）
     * @throws RuntimeException 手机号已注册时抛出
     */
    void register(RegisterDTO dto);

    /**
     * 客户登录
     *
     * 核心逻辑：BCrypt验证密码 → SaToken登录 → 返回Token
     *
     * @param dto 登录信息（手机号 + 密码）
     * @return 客户登录信息VO（含Token）
     * @throws RuntimeException 手机号未注册/密码错误/账号被禁用时抛出
     */
    CustomerLoginVO login(LoginDTO dto);

    /**
     * 修改客户个人资料
     *
     * @param dto 新的个人资料信息
     */
    void updateProfile(UpdateProfileDTO dto);

    /**
     * 修改密码
     *
     * 核心逻辑：先验证旧密码，再BCrypt编码新密码后更新
     *
     * @param dto 新旧密码
     * @throws RuntimeException 旧密码错误时抛出
     */
    void changePassword(ChangePasswordDTO dto);
}
