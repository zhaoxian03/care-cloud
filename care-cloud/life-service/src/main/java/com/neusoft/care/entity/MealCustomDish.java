package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 膳食记录与菜品关联表 实体类 - 对应数据库 meal_custom_dish 表
 * 
 * 功能说明：记录膳食定制中包含的菜品
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("meal_custom_dish")
@Schema(description = "膳食记录与菜品关联表")
public class MealCustomDish implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /** 膳食记录ID */
    @TableField("meal_custom_id")
    @Schema(description = "膳食记录ID", example = "1")
    private Long mealCustomId;

    /** 菜品ID */
    @TableField("dish_id")
    @Schema(description = "菜品ID", example = "1")
    private Integer dishId;

    /** 菜品名称（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "菜品名称")
    private String dishName;
}
