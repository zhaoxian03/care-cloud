package com.neusoft.care.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 管理员信息VO - 返回给前端的管理员详情
 * 
 * 功能说明：包含管理员基本信息，密码不返回
 * 实现Serializable接口，支持Redis缓存序列化
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 管理员ID */
    private Long id;
    
    /** 登录账号 */
    private String username;
    
    /** 真实姓名 */
    private String realName;
    
    /** 手机号（脱敏后） */
    private String phone;
    
    /** 角色级别（super_admin/admin，仅用于拦截器快速判断） */
    private String roleLevel;
    
    /** 状态（1-启用，0-禁用） */
    private Integer status;
    
    /** 创建者ID */
    private Long creatorId;

    /** RBAC角色名称列表（来自admin_role+role表） */
    private List<String> roleNames;

}
