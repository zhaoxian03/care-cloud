package com.neusoft.care.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色-权限关联实体类 - 对应数据库 role_permission 表
 *
 * 核心逻辑：
 * 1. 多对多关联关系：一个角色可以关联多个权限，一个权限可以被多个角色关联
 * 2. 通过roleId和permissionId两个外键建立关联
 *
 * 注意事项：无业务方法，仅用作MyBatis-Plus数据映射；实现Serializable接口支持序列化
 *
 * @author CareCenter Team
 */
@Data
@TableName("role_permission")
public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long permissionId;
}
