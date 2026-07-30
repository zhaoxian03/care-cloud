package com.neusoft.care.user.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.dto.AdminLoginDTO;
import com.neusoft.care.user.dto.CreateAdminDTO;
import com.neusoft.care.user.vo.AdminLoginVO;
import com.neusoft.care.user.vo.AdminVO;

/**
 * 管理员服务接口 - 定义管理员相关的业务方法
 * 
 * 功能说明：管理员登录、管理员管理等业务接口
 * 
 * @author CareCenter Team
 */
public interface AdminService {
    
    /** 管理员登录 */
    AdminLoginVO login(AdminLoginDTO dto);
    
    /** 管理员退出登录 */
    void logout();
    
    /** 获取当前登录管理员信息 */
    AdminVO getCurrentAdmin();
    
    /** 刷新Token */
    String refreshToken();
    
    /** 创建管理员（仅超级管理员可操作） */
    void createAdmin(CreateAdminDTO dto, Long creatorId);

    /** 创建健康管家（管理员及以上均可操作） */
    void createCaregiver(CreateAdminDTO dto, Long creatorId);
    
    /** 分页查询管理员列表 */
    PageResult<AdminVO> pageAdmins(Integer page, Integer size, String keyword);
    
    /** 更新管理员状态 */
    void updateAdminStatus(Long adminId, Integer status);
    
    /** 删除管理员 */
    void deleteAdmin(Long adminId, Long currentAdminId);
}
