package com.neusoft.care.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员登录响应VO - 返回给前端的登录结果
 * 
 * 功能说明：登录成功后返回管理员信息、JWT Token和菜单树
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginVO {
    /** 管理员ID */
    private Long id;
    
    /** 登录账号 */
    private String username;
    
    /** 真实姓名 */
    private String realName;
    
    /** 角色级别（super_admin/admin） */
    private String roleLevel;
    
    /** JWT Token（有效期7200秒） */
    private String token;

    /** 菜单树（根据RBAC权限动态生成） */
    private List<MenuNode> menu;
}
