package com.neusoft.care.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 注册请求DTO - 用于用户注册接口
 * 
 * 功能说明：封装新用户注册时提交的信息
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    /** 手机号（11位数字，将作为登录账号） */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    /** 密码（至少6位，将使用BCrypt加密存储） */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /** 真实姓名 */
    @NotBlank(message = "姓名不能为空")
    private String realName;
    
    /** 年龄 */
    @NotNull(message = "年龄不能为空")
    private Integer age;
    
    /** 性别（男/女） */
    @NotNull(message = "性别不能为空")
    private String gender;
}