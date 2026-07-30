package com.neusoft.care.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户信息VO - 返回给前端的客户详情
 * 
 * 功能说明：包含客户基本信息，手机号脱敏显示
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerVO {
    /** 客户ID */
    private Long id;
    
    /** 手机号（脱敏后） */
    private String phone;
    
    /** 真实姓名 */
    private String realName;
    
    /** 年龄 */
    private Integer age;
    
    /** 性别（男/女） */
    private String gender;
    
    /** 紧急联系人手机（脱敏后） */
    private String emergencyContact;
    
    /** 与紧急联系人关系 */
    private String emergencyRelation;
    
    /** 自理能力（自理/介助/介护） */
    private String selfCareAbility;
    
    /** 头像URL */
    private String avatarUrl;
    
    /** 状态（1-启用，0-禁用） */
    private Integer status;
}
