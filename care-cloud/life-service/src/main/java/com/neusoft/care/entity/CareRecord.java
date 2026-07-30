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
 * 护理记录表 实体类 - 对应数据库 care_record 表
 * 
 * 功能说明：记录护理人员对客户的护理执行情况
 * 
 * @author CareCenter Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("care_record")
@Schema(description = "护理记录表")
public class CareRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID", example = "1")
    private Long id;

    /** 客户ID，关联 customer 表 */
    @TableField("customer_id")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    /** 护理员ID（管理员），关联 admin 表 */
    @TableField("admin_id")
    @Schema(description = "护理员ID", example = "1")
    private Long adminId;

    /** 护理项目ID，关联 care_item 表 */
    @TableField("care_item_id")
    @Schema(description = "护理项目ID", example = "1")
    private Integer careItemId;

    /** 执行日期 */
    @TableField("record_date")
    @Schema(description = "执行日期", example = "2025-06-15")
    private LocalDate recordDate;

    /** 执行时间 */
    @TableField("record_time")
    @Schema(description = "执行时间", example = "14:30:00")
    private LocalTime recordTime;

    /** 状态（0-待执行，1-执行中，2-已完成） */
    @TableField("status")
    @Schema(description = "状态", example = "0")
    private Integer status;

    /** 备注 */
    @TableField("remark")
    @Schema(description = "备注", example = "客户状态良好")
    private String remark;

    /** 逻辑删除标志（0-正常，1-已删除） */
    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "逻辑删除标志", example = "0")
    private Integer isDeleted;

    /** 客户姓名（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "客户姓名")
    private String userName;

    /** 护理项目名称（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "护理项目名称")
    private String careItemName;

    /** 护理人员姓名（关联查询，非数据库字段） */
    @TableField(exist = false)
    @Schema(description = "护理人员姓名")
    private String nurseName;
}
