package com.neusoft.care.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 管理员实体类 - 对应数据库 admin 表
 * 
 * 功能说明：存储系统管理员信息，包括超级管理员和普通管理员
 * 
 * @author CareCenter Team
 * Serializable标志接口  用于序列化和反序列化 可用于redis缓存，sa-token，fegin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("admin")
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 管理员ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号（唯一） */
    private String username;

    /** 密码（BCrypt加密存储） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 角色级别（super_admin-超级管理员，admin-普通管理员） */
    private String roleLevel;

    /** 状态（1-启用，0-禁用） */
    private Integer status;

    /** 创建者ID（仅super_admin可创建） */
    private Long creatorId;

    /** 创建日期 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalTime createTime;

    /** 更新日期 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate updateDate;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalTime updateTime;
}
