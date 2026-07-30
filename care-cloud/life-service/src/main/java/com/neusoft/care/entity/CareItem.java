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
 * 护理项目表 实体类 - 对应数据库 care_item 表
 * 
 * 功能说明：存储养老院护理项目信息，如翻身、喂药、测血压等
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("care_item")
@Schema(description = "护理项目表")
public class CareItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 护理项目ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "护理项目ID", example = "1")
    private Integer id;

    /** 项目名称（唯一） */
    @TableField("item_name")
    @Schema(description = "项目名称", example = "翻身护理")
    private String itemName;

    /** 预计耗时（分钟） */
    @TableField("default_duration_minutes")
    @Schema(description = "预计耗时（分钟）", example = "30")
    private Integer defaultDurationMinutes;

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
