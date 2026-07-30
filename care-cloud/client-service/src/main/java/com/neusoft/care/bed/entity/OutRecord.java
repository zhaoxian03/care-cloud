package com.neusoft.care.bed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 外出记录实体类 - 对应数据库 out_record 表
 * 
 * 功能说明：记录客户的外出和返回信息
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("out_record")
public class OutRecord {

    /** 记录ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 客户ID，关联 customer 表 */
    private Long customerId;

    /** 外出日期 */
    private LocalDate outDate;

    /** 外出时间 */
    private LocalTime outTime;

    /** 预计返回日期 */
    private LocalDate expectedBackDate;

    /** 预计返回时间 */
    private LocalTime expectedBackTime;

    /** 实际返回日期 */
    private LocalDate actualBackDate;

    /** 实际返回时间 */
    private LocalTime actualBackTime;

    /** 状态（0-外出中，1-已返回，2-超时） */
    private Integer status;

    /** 外出原因 */
    private String reason;

    /** 逻辑删除标志（0-正常，1-已删除） */
    @TableLogic
    private Integer isDeleted;

    /** 客户姓名（关联查询，非数据库字段） */
    @TableField(exist = false)
    private String realName;
}
