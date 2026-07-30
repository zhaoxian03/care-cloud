package com.neusoft.care.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 创建客户请求DTO - 用于新增客户接口
 * 
 * 功能说明：管理员创建客户时提交的数据
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerDTO {
    /** 手机号（唯一标识） */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;
    
    /** 真实姓名 */
    @NotBlank(message = "姓名不能为空")
    private String realName;
    
    /** 年龄 */
    @NotNull(message = "年龄不能为空")
    private Integer age;
    
    /** 性别（男/女） */
    @NotNull(message = "性别不能为空")
    private String gender;
    
    /** 紧急联系人手机号 */
    private String emergencyContact;
    
    /** 与紧急联系人关系 */
    private String emergencyRelation;
    
    /** 自理能力（自理/介助/介护） */
    private String selfCareAbility;
    
    /** 头像URL（可选） */
    private String avatarUrl;
}
