package com.neusoft.care.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.user.dto.CreateCustomerDTO;
import com.neusoft.care.common.entity.Customer;
import com.neusoft.care.common.mapper.CustomerMapper;
import com.neusoft.care.user.feign.BedServiceFeignClient;
import com.neusoft.care.user.vo.CustomerVO;
import com.neusoft.care.user.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.neusoft.care.common.exception.BusinessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 客户服务实现类 - 实现客户管理的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 创建客户：手机号唯一校验 → 设置默认状态(status=1启用) → 插入记录
 * 2. 客户分页查询：支持按姓名/手机号模糊搜索、按状态筛选
 * 3. 修改客户：校验客户存在性 + 手机号唯一性（排除自身）
 * 4. 禁用客户：调用BedServiceFeignClient检查在住记录（activeCheckIn>0时禁止禁用）
 * 5. 删除客户：调用BedServiceFeignClient检查在住记录和未归外出记录后执行软删除
 *
 * 跨服务调用说明：禁用/删除客户前需通过Feign调用client-service检查床位使用情况
 * 注意事项：手机号校验逻辑——更新时排除自身ID避免误判
 *
 * @author CareCenter Team
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private BedServiceFeignClient bedServiceFeignClient;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 创建客户
     *
     * 核心逻辑：
     * 1. 校验手机号是否已被使用
     * 2. 组装Customer实体，设置默认状态为启用（status=1）、未删除（isDeleted=0）
     * 3. 插入数据库
     *
     * @param dto 创建客户请求DTO（含手机号、姓名、年龄、性别等）
     * @throws RuntimeException 手机号已存在时抛出
     */
    @Override
    @Transactional
    public void createCustomer(CreateCustomerDTO dto) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, dto.getPhone());
        Long count = customerMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("手机号已存在");
        }

        Customer customer = new Customer();
        customer.setPhone(dto.getPhone());
        customer.setRealName(dto.getRealName());
        customer.setAge(dto.getAge());
        customer.setGender(dto.getGender());
        customer.setEmergencyContact(dto.getEmergencyContact());
        customer.setEmergencyRelation(dto.getEmergencyRelation());
        customer.setSelfCareAbility(dto.getSelfCareAbility());
        customer.setAvatarUrl(dto.getAvatarUrl());
        customer.setPassword(passwordEncoder.encode("123456"));
        customer.setStatus(1);
        customer.setIsDeleted(0);
        customerMapper.insert(customer);
    }

    /**
     * 客户分页查询
     *
     * 核心逻辑：
     * 1. 支持按姓名(realName)或手机号(phone)模糊搜索
     * 2. 支持按status筛选
     * 3. 结果按创建时间倒序排列
     * 4. Entity → VO 转换
     *
     * @param page 页码
     * @param size 每页条数
     * @param keyword 搜索关键词（匹配姓名或手机号，可为空）
     * @param status 状态筛选（可为空，表示不过滤）
     * @return 分页结果（含CustomerVO列表）
     */
    @Override
    public PageResult<CustomerVO> pageCustomers(Integer page, Integer size, String keyword, Integer status) {
        Page<Customer> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Customer::getRealName, keyword).or().like(Customer::getPhone, keyword));
        }

        if (status != null) {
            wrapper.eq(Customer::getStatus, status);
        }

        wrapper.orderByDesc(Customer::getCreateTime);

        IPage<Customer> customerPage = customerMapper.selectPage(pageParam, wrapper);

        List<CustomerVO> records = customerPage.getRecords().stream().map(customer -> {
            CustomerVO vo = new CustomerVO();
            vo.setId(customer.getId());
            vo.setPhone(customer.getPhone());
            vo.setRealName(customer.getRealName());
            vo.setAge(customer.getAge());
            vo.setGender(customer.getGender());
            vo.setEmergencyContact(customer.getEmergencyContact());
            vo.setEmergencyRelation(customer.getEmergencyRelation());
            vo.setSelfCareAbility(customer.getSelfCareAbility());
            vo.setAvatarUrl(customer.getAvatarUrl());
            vo.setStatus(customer.getStatus());
            return vo;
        }).collect(Collectors.toList());

        PageResult<CustomerVO> result = new PageResult<>();
        result.setTotal(customerPage.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 修改客户信息
     *
     * 核心逻辑：
     * 1. 校验客户是否存在
     * 2. 若修改手机号，需校验唯一性（排除自身ID）
     * 3. 更新各字段（允许修改手机号）
     *
     * @param id 客户ID
     * @param dto 新的客户信息
     * @throws RuntimeException 客户不存在或手机号已被其他客户占用时抛出
     */
    @Override
    @Transactional
    public void updateCustomer(Long id, CreateCustomerDTO dto) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, dto.getPhone());
        wrapper.ne(Customer::getId,id);

        long count = customerMapper.selectCount(wrapper);
        if(count > 0){
            throw new BusinessException("手机号已被占用");
        }

        customer.setRealName(dto.getRealName());
        customer.setAge(dto.getAge());
        customer.setGender(dto.getGender());
        customer.setPhone(dto.getPhone());
        customer.setEmergencyContact(dto.getEmergencyContact());
        customer.setEmergencyRelation(dto.getEmergencyRelation());
        customer.setSelfCareAbility(dto.getSelfCareAbility());
        customer.setAvatarUrl(dto.getAvatarUrl());
        customerMapper.updateById(customer);
    }

    /**
     * 更新客户状态（启用/禁用）
     *
     * 核心逻辑：
     * 1. 校验客户是否存在
     * 2. 若为禁用操作（status=0），通过Feign调用client-service检查是否有在住记录
     *    有在住记录时不允许禁用
     * 3. 更新状态
     *
     * 跨服务调用：调用BedServiceFeignClient.countActiveCheckIn查询在住记录数
     *
     * @param id 客户ID
     * @param status 目标状态（1-启用，0-禁用）
     * @throws RuntimeException 客户不存在或存在在住记录时抛出
     */
    @Override
    @Transactional
    public void updateCustomerStatus(Long id, Integer status) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        if (status == 0) {
            Long activeCount = bedServiceFeignClient.countActiveCheckIn(id);
            if (activeCount > 0) {
                throw new BusinessException("该客户有在住记录，无法禁用");
            }

        }

        customer.setStatus(status);
        customerMapper.updateById(customer);
    }

    /**
     * 删除客户（软删除）
     *
     * 核心逻辑：
     * 1. 校验客户是否存在
     * 2. 通过Feign调用client-service检查是否有在住记录（activeCheckIn>0时禁止删除）
     * 3. 通过Feign调用client-service检查是否有未归外出记录（activeOut>0时禁止删除）
     * 4. 执行物理删除（MyBatis-Plus的deleteById为物理删除）
     *
     * 跨服务调用：调用BedServiceFeignClient检查client-service端的入住和外出状态
     *
     * @param id 客户ID
     * @throws RuntimeException 客户不存在/有在住记录/有未归外出记录时抛出
     */
    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        Long activeCount = bedServiceFeignClient.countActiveCheckIn(id);
        if (activeCount > 0) {
            throw new BusinessException("该客户有在住记录，无法删除");
        }

        Long outCount = bedServiceFeignClient.countActiveOut(id);
        if (outCount > 0) {
            throw new BusinessException("该客户有未归外出记录，无法删除");
        }

        customerMapper.deleteById(id);
    }
}
