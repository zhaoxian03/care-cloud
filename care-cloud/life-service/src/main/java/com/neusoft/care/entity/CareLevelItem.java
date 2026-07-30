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
 * 护理级别与项目关联表 实体类 - 对应数据库 care_level_item 表
 * 
 * 功能说明：记录每个护理级别包含哪些护理项目，以及项目的执行顺序
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("care_level_item")
@Schema(description = "护理级别与项目关联表")
public class CareLevelItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id;

    /** 护理级别ID，关联 care_level 表 */
    @TableField("care_level_id")
    @Schema(description = "护理级别ID", example = "1")
    private Integer careLevelId;

    /** 护理项目ID，关联 care_item 表 */
    @TableField("care_item_id")
    @Schema(description = "护理项目ID", example = "1")
    private Integer careItemId;

    /** 执行顺序 */
    @TableField("sort_order")
    @Schema(description = "执行顺序", example = "1")
    private Integer sortOrder;

    /** 护理项目名称（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "护理项目名称")
    private String itemName;

    /** 预计耗时（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "预计耗时（分钟）")
    private Integer defaultDurationMinutes;
}
