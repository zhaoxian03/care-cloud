package com.neusoft.care.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * 管理员登录请求DTO - 用于管理员登录接口
 * 
 * 功能说明：封装管理员登录时提交的账号和密码
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor     // Jakarta Bean Validation（标准化 Bean 校验框架） 中的非空校验注解
public class AdminLoginDTO {
    /** 登录账号 */
    @NotBlank(message = "账号不能为空")
    private String username;
    
    /** 密码（明文） */
    @NotBlank(message = "密码不能为空")
    private String password;
}
