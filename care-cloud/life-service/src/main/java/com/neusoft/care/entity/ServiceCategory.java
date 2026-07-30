package com.neusoft.care.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 服务分类 —— 对服务产品进行归类聚合，例如"生活照料"、"医疗护理"等。
 * 管理员可在页面直接增删改，无需写代码。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("service_category")
@Schema(description = "服务分类表")
public class ServiceCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID，自增主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /** 分类名称，如"生活照料" */
    @TableField("name")
    @Schema(description = "分类名称", example = "生活照料")
    private String name;

    /** 排序号，数字越小越靠前 */
    @TableField("sort")
    @Schema(description = "排序号", example = "1")
    private Integer sort;

    /** 逻辑删除标志：0-正常，1-已删除 */
    @TableLogic
    @TableField("is_deleted")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "逻辑删除标志", example = "0")
    private Integer isDeleted;
}
