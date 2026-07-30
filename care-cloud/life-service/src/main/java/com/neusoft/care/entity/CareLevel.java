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
import java.math.BigDecimal;

/**
 * 护理级别表 实体类 - 对应数据库 care_level 表
 * 
 * 功能说明：存储养老院护理级别信息，如特级护理、一级护理等
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("care_level")
@Schema(description = "护理级别表")
public class CareLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 护理级别ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "护理级别ID", example = "1")
    private Integer id;

    /** 级别名称（唯一） */
    @TableField("level_name")
    @Schema(description = "级别名称", example = "特级护理")
    private String levelName;

    /** 每日费用 */
    @TableField("price")
    @Schema(description = "每日费用", example = "500.00")
    private BigDecimal price;

    /** 描述 */
    @TableField("description")
    @Schema(description = "描述", example = "24小时一对一护理")
    private String description;

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
