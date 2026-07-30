package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 菜品表 实体类 - 对应数据库 dish 表
 * 
 * 功能说明：存储菜品信息，用于膳食定制时选择
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dish")
@Schema(description = "菜品表")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 菜品ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "菜品ID", example = "1")
    private Integer id;

    /** 菜品名称（唯一） */
    @TableField("name")
    @Schema(description = "菜品名称", example = "小米粥")
    private String name;

    /** 分类（主食/热菜/凉菜/汤/水果等） */
    @TableField("category")
    @Schema(description = "分类", example = "主食")
    private String category;

    /** 是否启用（1-启用，0-禁用） */
    @TableField("is_active")
    @Schema(description = "是否启用", example = "1")
    private Integer isActive;

    /** 逻辑删除标志（0-正常，1-已删除） */
    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "逻辑删除标志", example = "0")
    private Integer isDeleted;
}
