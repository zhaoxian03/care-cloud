package com.neusoft.care.bed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 入住记录实体类 - 对应数据库 check_in_record 表
 * 
 * 功能说明：记录客户的入住、退住信息，关联用户、床位和护理级别
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("check_in_record")
public class CheckInRecord {

    /** 记录ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 客户ID，关联 customer 表 */
    private Long customerId;

    /** 床位ID，关联 bed 表 */
    private Long bedId;

    /** 护理级别ID，关联 care_level 表 */
    private Integer careLevelId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退住日期 */
    private LocalDate checkOutDate;

    /** 状态（0-入住中，1-已退住，2-外出中） */
    private Integer status;

    /** 逻辑删除标志（0-正常，1-已删除） */
    @TableLogic
    private Integer isDeleted;

    /** 创建日期，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDate createDate;

    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalTime createTime;

    /** 客户姓名（关联查询，非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 房间号（关联查询，非数据库字段） */
    @TableField(exist = false)
    private String roomNumber;

    /** 床号（关联查询，非数据库字段） */
    @TableField(exist = false)
    private String bedNumber;

    /** 护理级别名称（关联查询，非数据库字段） */
    @TableField(exist = false)
    private String careLevelName;
}
