package com.neusoft.care.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 管家管理视图对象 —— 用于管理端管家列表展示（admin信息 + 已服务老人数统计）
 *
 * @author CareCenter Team
 */
@Data
@Schema(description = "管家管理视图对象（admin + caregiver_relation 联合）")
public class CaregiverManageVO {

    @Schema(description = "管家ID（admin.id）")
    private Long id;

    @Schema(description = "管家姓名")
    private String realName;

    @Schema(description = "管家手机号")
    private String phone;

    @Schema(description = "状态（1-启用，0-禁用）")
    private Integer status;

    @Schema(description = "已服务老人数")
    private Long boundElderCount;

    @Schema(description = "创建日期")
    private LocalDate createDate;

    @Schema(description = "创建时间")
    private LocalTime createTime;
}
