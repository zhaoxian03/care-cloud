package com.neusoft.care.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.common.dto.LoginDTO;
import com.neusoft.care.common.dto.RegisterDTO;
import com.neusoft.care.common.entity.Customer;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.common.mapper.CustomerMapper;
import com.neusoft.care.user.dto.ChangePasswordDTO;
import com.neusoft.care.user.dto.UpdateProfileDTO;
import com.neusoft.care.user.service.AuthService;
import com.neusoft.care.user.vo.CustomerLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户认证服务实现类 - 实现客户端注册、登录及个人信息维护的业务逻辑
 *
 * 核心逻辑：
 * 1. 注册流程：手机号唯一校验 → BCrypt编码密码 → 插入Customer记录（status=1启用）
 * 2. 登录流程：手机号查询 → BCrypt密码验证 → 状态校验 → SaToken签发Token → 返回客户信息+Token
 * 3. 修改资料：从SaToken获取当前登录客户ID，校验手机号唯一性后更新
 * 4. 修改密码：先验证旧密码，再BCrypt编码新密码后更新
 *
 * 注意事项：所有密码操作使用BCrypt加密，登录状态由SaToken管理，登录类型为"customer"
 *
 * @author CareCenter Team
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 客户注册
     *
     * 核心逻辑：
     * 1. 校验手机号是否已注册（全局唯一）
     * 2. BCrypt编码密码后存入数据库
     * 3. 设置默认状态为启用（status=1）、未删除（isDeleted=0）
     *
     * @param dto 注册信息（含手机号、密码、姓名、年龄、性别）
     * @throws RuntimeException 手机号已注册时抛出
     */
    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, dto.getPhone());
        if (customerMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("手机号已注册");
        }

        Customer customer = new Customer();
        customer.setPhone(dto.getPhone());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setRealName(dto.getRealName());
        customer.setAge(dto.getAge());
        customer.setGender(dto.getGender());
        customer.setStatus(1);
        customer.setIsDeleted(0);
        customerMapper.insert(customer);
    }

    /**
     * 客户登录
     *
     * 核心逻辑：
     * 1. 手机号查询客户记录
     * 2. BCrypt验证密码
     * 3. 校验账号状态（status=1为启用）
     * 4. SaToken登录（登录类型为"customer"）
     * 5. 获取Token值并组装响应VO
     *
     * @param dto 登录信息（手机号 + 密码）
     * @return 客户登录信息VO（含客户基本信息 + Token）
     * @throws RuntimeException 手机号未注册/密码错误/账号被禁用时抛出
     */
    @Override
    public CustomerLoginVO login(LoginDTO dto) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, dto.getPhone());
        Customer customer = customerMapper.selectOne(wrapper);

        if (customer == null) {
            throw new BusinessException("手机号未注册");
        }
        if (customer.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (customer.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        StpUtil.login(customer.getId(), "customer");
        String token = StpUtil.getTokenValue();

        CustomerLoginVO vo = new CustomerLoginVO();
        vo.setId(customer.getId());
        vo.setPhone(customer.getPhone());
        vo.setRealName(customer.getRealName());
        vo.setAge(customer.getAge());
        vo.setGender(customer.getGender());
        vo.setAvatarUrl(customer.getAvatarUrl());
        vo.setEmergencyContact(customer.getEmergencyContact());
        vo.setEmergencyRelation(customer.getEmergencyRelation());
        vo.setToken(token);
        return vo;
    }

    /**
     * 修改客户个人资料
     *
     * 核心逻辑：
     * 1. 从SaToken获取当前登录客户ID（登录类型"customer"）
     * 2. 若修改手机号，需校验唯一性（排除自身）
     * 3. 更新各字段（仅更新非null字段）
     *
     * @param dto 新的个人资料信息（字段可选）
     * @throws RuntimeException 客户不存在时抛出
     */
    @Override
    @Transactional
    public void updateProfile(UpdateProfileDTO dto) {
        Long customerId = Long.valueOf(String.valueOf(StpUtil.getLoginId("customer")));
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (dto.getPhone() != null && !dto.getPhone().equals(customer.getPhone())) {
            LambdaQueryWrapper<Customer> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(Customer::getPhone, dto.getPhone());
            if (customerMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException("账号或密码错误");
            }
            customer.setPhone(dto.getPhone());
        }
        if (dto.getRealName() != null) customer.setRealName(dto.getRealName());
        if (dto.getAge() != null) customer.setAge(dto.getAge());
        if (dto.getGender() != null) customer.setGender(dto.getGender());
        if (dto.getAvatarUrl() != null) customer.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getEmergencyContact() != null) customer.setEmergencyContact(dto.getEmergencyContact());
        if (dto.getEmergencyRelation() != null) customer.setEmergencyRelation(dto.getEmergencyRelation());
        customerMapper.updateById(customer);
    }

    /**
     * 修改密码
     *
     * 核心逻辑：
     * 1. 从SaToken获取当前登录客户ID
     * 2. 先BCrypt验证旧密码
     * 3. 再BCrypt编码新密码并更新
     *
     * @param dto 新旧密码
     * @throws RuntimeException 客户不存在或旧密码错误时抛出
     */
    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO dto) {
        Long customerId = Long.valueOf(String.valueOf(StpUtil.getLoginId("customer")));
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), customer.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        customer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        customerMapper.updateById(customer);
    }
}
