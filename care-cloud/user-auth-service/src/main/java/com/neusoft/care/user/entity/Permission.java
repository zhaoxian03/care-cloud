package com.neusoft.care.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 权限实体类 - 对应数据库 permission 表
 *
 * 核心逻辑：
 * 1. 树形结构：通过parentId自关联实现父子层级
 * 2. 权限类型（type）：MENU（菜单）、BUTTON（按钮）、API（接口）
 * 3. 排序字段sort控制同级节点展示顺序
 * 4. children字段为@TableField(exist=false)，仅用于树形展示，不存入数据库
 *
 * 注意事项：权限编码（code）全局唯一，用作@SaCheckPermission注解的校验值
 *
 * @author CareCenter Team
 */
@Data
@TableName("permission")
public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String type;

    private Long parentId;

    private String path;

    private String icon;

    private String backUrl;

    private Integer sort;

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

    @TableField(exist = false)
    private List<Permission> children;
}
