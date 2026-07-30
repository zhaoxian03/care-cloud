package com.neusoft.care.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 客户实体类 - 对应数据库 customer 表
 * 
 * 功能说明：存储养老院客户信息
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("customer")
public class Customer {

    /** 客户ID，自增主键 */
    @TableId(type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /** 手机号（唯一标识） */
    private String phone;

    /** 登录密码（BCrypt加密） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 年龄 */
    private Integer age;

    /** 性别（男/女） */
    private String gender;

    /** 紧急联系人手机号 */
    private String emergencyContact;

    /** 与紧急联系人关系 */
    private String emergencyRelation;

    /** 自理能力（自理/介助/介护） */
    private String selfCareAbility;

    /** 头像URL */
    private String avatarUrl;

    /** 状态（1-启用，0-禁用） */
    private Integer status;

    /** 逻辑删除标志（0-正常，1-已删除） */
    @TableLogic
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer isDeleted;

    /** 创建日期 */
    @TableField(fill = FieldFill.INSERT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createDate;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalTime createTime;
}
