package com.neusoft.care.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 护理统计 VO
 * 
 * 功能说明：返回护理统计数据
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "护理统计")
public class CareStatsVO {

    /** 今日护理总数 */
    @Schema(description = "今日护理总数")
    private Long todayCount;

    /** 待执行数量 */
    @Schema(description = "待执行数量")
    private Long pendingCount;

    /** 执行中数量 */
    @Schema(description = "执行中数量")
    private Long inProgressCount;

    /** 已完成数量 */
    @Schema(description = "已完成数量")
    private Long completedCount;
}
