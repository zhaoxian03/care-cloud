package com.neusoft.care.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO - 用于用户登录接口
 * 
 * 功能说明：封装用户登录时提交的手机号和密码
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    /** 手机号（登录账号） */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    /** 密码（明文） */
    @NotBlank(message = "密码不能为空")
    private String password;
}