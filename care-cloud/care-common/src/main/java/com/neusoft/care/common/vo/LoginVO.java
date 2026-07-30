package com.neusoft.care.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO - 返回给前端的登录结果
 * 
 * 功能说明：登录成功后返回用户信息和JWT Token
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVO {

    /** 用户ID */
    private Long id;

    /** 手机号（脱敏后） */
    private String phone;

    /** 真实姓名 */
    private String realName;

    /** 角色编码（admin/nurse/caregiver/elder/family） */
    private String role;

    /** JWT Token（有效期7200秒） */
    private String token;
}
