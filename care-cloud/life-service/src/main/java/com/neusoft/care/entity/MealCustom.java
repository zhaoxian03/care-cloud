package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 膳食定制表 实体类 - 对应数据库 meal_custom 表
 * 
 * 功能说明：记录客户的膳食定制信息，支持按日期和餐次管理
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("meal_custom")
@Schema(description = "膳食定制表")
public class MealCustom implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID", example = "1")
    private Long id;

    /** 客户ID，关联 customer 表 */
    @TableField("customer_id")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    /** 膳食日期 */
    @TableField("meal_date")
    @Schema(description = "膳食日期", example = "2025-06-15")
    private LocalDate mealDate;

    /** 膳食类型（1-早餐，2-午餐，3-晚餐） */
    @TableField("meal_type")
    @Schema(description = "膳食类型", example = "1")
    private Integer mealType;

    /** 状态（0-启用，1-停用） */
    @TableField("status")
    @Schema(description = "状态", example = "0")
    private Integer status;

    /** 创建日期 */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建日期")
    private LocalDate createDate;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalTime createTime;

    /** 逻辑删除标志（0-正常，1-已删除） */
    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "逻辑删除标志", example = "0")
    private Integer isDeleted;

    /** 客户姓名（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "客户姓名")
    private String customerName;

    /** 菜品ID列表（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "菜品ID列表")
    private List<Integer> dishIds;

    /** 菜品名称列表（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "菜品名称列表")
    private List<String> dishNames;
}
