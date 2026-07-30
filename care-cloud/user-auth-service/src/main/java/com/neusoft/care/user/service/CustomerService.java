package com.neusoft.care.user.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.user.dto.CreateCustomerDTO;
import com.neusoft.care.user.vo.CustomerVO;

/**
 * 客户服务接口 - 定义客户管理的业务方法
 * 
 * 功能说明：客户创建、查询、修改、删除等业务接口
 * 
 * @author CareCenter Team
 */
public interface CustomerService {
    
    /** 创建客户 */
    void createCustomer(CreateCustomerDTO dto);
    
    /** 分页查询客户列表 */
    PageResult<CustomerVO> pageCustomers(Integer page, Integer size, String keyword, Integer status);
    
    /** 修改客户信息 */
    void updateCustomer(Long id, CreateCustomerDTO dto);
    
    /** 更新客户状态（启用/禁用） */
    void updateCustomerStatus(Long id, Integer status);
    
    /** 删除客户（软删除） */
    void deleteCustomer(Long id);
}
