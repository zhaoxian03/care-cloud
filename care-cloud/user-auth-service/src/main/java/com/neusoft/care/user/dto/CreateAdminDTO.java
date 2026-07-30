package com.neusoft.care.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
/**
 * 创建管理员请求DTO - 用于新增管理员接口
 * 
 * 功能说明：超级管理员创建普通管理员时提交的数据
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdminDTO {
    /** 登录账号（唯一） */
    @NotBlank(message = "账号不能为空")
    private String username;
    
    /** 密码（至少6位） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /** 真实姓名 */
    @NotBlank(message = "姓名不能为空")
    private String realName;
    
    /** 手机号 */
    private String phone;
    
    /** 角色级别（只能创建admin，不能创建super_admin） */
    private String roleLevel;
}
