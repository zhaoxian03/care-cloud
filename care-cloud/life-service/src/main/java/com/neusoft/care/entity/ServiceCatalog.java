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
 * 服务产品目录 —— 统一定义所有可订阅的服务项目，如"房间清洁"、"定制营养餐"。
 * 订阅时从目录复制价格快照到 customer_subscription.price，后续调价不影响已有订阅。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("service_catalog")
@Schema(description = "服务产品目录表")
public class ServiceCatalog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 服务ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "服务ID", example = "1")
    private Long id;

    /** 所属分类ID，逻辑关联 service_category.id */
    @TableField("category_id")
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /** 服务名称，如"房间清洁" */
    @TableField("name")
    @Schema(description = "服务名称", example = "房间清洁")
    private String name;

    /** 服务描述，说明服务内容/频次等 */
    @TableField("description")
    @Schema(description = "服务描述", example = "每周一次深度清洁")
    private String description;

    /** 定价，订阅时复制到 customer_subscription.price 作为快照 */
    @TableField("price")
    @Schema(description = "定价", example = "200.00")
    private BigDecimal price;

    /** 计价单位：once(次)/day(日)/month(月)/year(年)/long(长期) */
    @TableField("unit")
    @Schema(description = "计价单位", example = "month")
    private String unit;

    /** 是否上架：1-上架可订阅，0-下架不可选 */
    @TableField("is_active")
    @Schema(description = "是否上架", example = "1")
    private Integer isActive;

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

    /** 所属分类名称（非数据库字段，联表查询时填充） */
    @TableField(exist = false)
    private String categoryName;
}
