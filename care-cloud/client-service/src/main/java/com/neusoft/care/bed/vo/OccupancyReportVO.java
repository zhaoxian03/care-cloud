package com.neusoft.care.bed.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 入住统计报表 VO
 * 
 * 功能说明：返回入住统计数据
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupancyReportVO {

    /** 总床位数 */
    private Long totalBeds;

    /** 已占用床位数 */
    private Long occupiedBeds;

    /** 空闲床位数 */
    private Long freeBeds;

    /** 入住率（百分比） */
    private Double occupancyRate;

    /** 客户总数 */
    private Long totalCustomers;

    /** 在住人数 */
    private Long checkedInCount;

    /** 外出人数 */
    private Long outCount;

    /** 逾期未归人数 */
    private Long overdueCount;
}
