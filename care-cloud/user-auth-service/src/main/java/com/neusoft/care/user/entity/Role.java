package com.neusoft.care.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 角色实体类 - 对应数据库 role 表
 *
 * 核心逻辑：
 * 1. 角色定义：包含名称（name）、编码（code）、备注（remark）
 * 2. 系统预置角色（isSystem=1）受保护：不可修改编码、不可删除
 * 3. 启用/禁用控制（isDisabled）
 *
 * 注意事项：角色编码（code）全局唯一，用作SaToken权限校验的标识
 *
 * @author CareCenter Team
 */
@Data
@TableName("role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String remark;

    private Integer isSystem;

    private Integer isDisabled;

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
