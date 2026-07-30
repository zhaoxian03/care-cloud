package com.neusoft.care.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员-角色关联实体类 - 对应数据库 admin_role 表
 *
 * 核心逻辑：
 * 1. 多对多关联关系：一个管理员可以关联多个角色，一个角色可以被多个管理员关联
 * 2. RBAC权限模型的核心关联表，用于确定管理员具有哪些角色的权限
 *
 * 注意事项：无业务方法，仅用作MyBatis-Plus数据映射
 *
 * @author CareCenter Team
 */
@Data
@TableName("admin_role")
public class AdminRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private Long roleId;
}
