package com.neusoft.care.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 健康管家视图对象 —— 用于前端展示绑定关系（关联记录ID、管家信息、绑定时间）
 *
 * @author CareCenter Team
 */
@Data
@Schema(description = "健康管家视图对象")
public class CaregiverVO {

    @Schema(description = "关联记录ID")
    private Long relationId;

    @Schema(description = "管家ID")
    private Long adminId;

    @Schema(description = "管家姓名")
    private String realName;

    @Schema(description = "管家手机号")
    private String phone;

    @Schema(description = "绑定时间")
    private LocalDate createDate;

    @Schema(description = "绑定时间（时分秒）")
    private LocalTime createTime;
}
