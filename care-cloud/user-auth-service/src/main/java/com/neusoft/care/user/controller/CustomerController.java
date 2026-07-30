package com.neusoft.care.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.user.dto.CreateCustomerDTO;
import com.neusoft.care.user.vo.CustomerVO;
import com.neusoft.care.user.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 客户控制器 - 处理客户管理相关的所有HTTP请求
 * 
 * 功能说明：
 * 1. 创建客户：管理员创建客户记录
 * 2. 客户分页查询：支持关键词搜索和状态筛选
 * 3. 修改客户信息：修改客户基本信息
 * 4. 更新客户状态：启用/禁用客户
 * 5. 删除客户：软删除客户记录
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 创建客户接口
     * URL: POST /api/customer
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("customer:create")
    @PostMapping
    public Result<Void> createCustomer(@Valid @RequestBody CreateCustomerDTO dto) {
        customerService.createCustomer(dto);
        return Result.success();
    }

    /**
     * 客户分页查询接口
     * URL: GET /api/customer/page
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("customer:list")
    @GetMapping("/page")
    public Result<PageResult<CustomerVO>> pageCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<CustomerVO> result = customerService.pageCustomers(page, size, keyword, status);
        return Result.success(result);
    }

    /**
     * 修改客户信息接口
     * URL: PUT /api/customer/{id}
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("customer:edit")
    @PutMapping("/{id}")
    public Result<Void> updateCustomer(@PathVariable Long id, @Valid @RequestBody CreateCustomerDTO dto) {
        customerService.updateCustomer(id, dto);
        return Result.success();
    }

    /**
     * 更新客户状态接口
     * URL: PUT /api/customer/{id}/status
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("customer:status")
    @PutMapping("/{id}/status")
    public Result<Void> updateCustomerStatus(@PathVariable Long id, @RequestParam Integer status) {
        customerService.updateCustomerStatus(id, status);
        return Result.success();
    }

    /**
     * 删除客户接口（软删除）
     * URL: DELETE /api/customer/{id}
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("customer:delete")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }
}
