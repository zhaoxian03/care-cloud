package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 健康管家关联实体 —— 对应数据库 caregiver_relation 表
 *
 * 核心逻辑：
 * 1. 记录客户（customer_id）与健康管家（admin_id）的绑定关系
 * 2. 使用逻辑删除（is_deleted），解绑时标记为已删除而非物理删除
 *
 * 注意事项：绑定数量由配置项控制（caregiver.max-per-elder / caregiver.max-elders-per-caregiver）
 *
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("caregiver_relation")
@Schema(description = "健康管家关联表")
public class CaregiverRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID", example = "1")
    private Long id;

    @TableField("customer_id")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @TableField("admin_id")
    @Schema(description = "健康管家ID", example = "1")
    private Long adminId;

    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "逻辑删除标志", example = "0")
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建日期")
    private LocalDate createDate;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalTime createTime;
}
