package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 客户订阅记录 —— 记录"哪位客户买了/被分配了哪些服务"。
 * 不与护理记录、膳食定制等现有功能耦合，仅用于统一看板和到期提醒。
 * 价格快照在订阅时从 service_catalog.price 复制，后续调价不影响已有订阅。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer_subscription")
@Schema(description = "客户订阅记录表")
public class CustomerSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，自增 */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /** 客户ID，逻辑关联 customer.id */
    @TableField("customer_id")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    /** 服务产品ID，逻辑关联 service_catalog.id */
    @TableField("catalog_id")
    @Schema(description = "服务产品ID", example = "1")
    private Long catalogId;

    /** 订阅开始日期 */
    @TableField("start_date")
    @Schema(description = "订阅开始日期", example = "2025-01-01")
    private LocalDate startDate;

    /** 订阅到期日期，null 表示长期有效 */
    @TableField("end_date")
    @Schema(description = "订阅到期日期", example = "2025-12-31")
    private LocalDate endDate;

    /** 订阅状态：ACTIVE-活跃，EXPIRED-已过期，CANCELLED-已取消 */
    @TableField("status")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "订阅状态：ACTIVE/EXPIRED/CANCELLED", example = "ACTIVE")
    private String status;

    /** 订阅时价格快照（从 service_catalog.price 复制），后续调价不影响已有订阅 */
    @TableField("price")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "订阅时价格快照", example = "200.00")
    private BigDecimal price;

    /** 创建日期（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "创建日期")
    private LocalDate createDate;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "创建时间")
    private LocalTime createTime;

    /** 修改日期（自动填充） */
    @TableField(fill = FieldFill.UPDATE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "修改日期")
    private LocalDate updateDate;

    /** 修改时间（自动填充） */
    @TableField(fill = FieldFill.UPDATE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "修改时间")
    private LocalTime updateTime;

    /** 逻辑删除标志：0-正常，1-已删除 */
    @TableLogic
    @TableField("is_deleted")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "逻辑删除标志", example = "0")
    private Integer isDeleted;

    /** 客户姓名（非数据库字段，分页查询时通过 customer_id 填充） */
    @TableField(exist = false)
    private String customerName;

    /** 服务名称（非数据库字段，分页查询时通过 catalog_id 填充） */
    @TableField(exist = false)
    private String catalogName;

    /** 分类名称（非数据库字段，分页查询时通过 catalog.category_id 填充） */
    @TableField(exist = false)
    private String categoryName;

    /** 服务计价单位（非数据库字段） */
    @TableField(exist = false)
    private String catalogUnit;

    /** 服务单价（非数据库字段） */
    @TableField(exist = false)
    private java.math.BigDecimal catalogPrice;
}
