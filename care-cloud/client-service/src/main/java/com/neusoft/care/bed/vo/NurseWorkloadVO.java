package com.neusoft.care.bed.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 护理工作量统计 VO
 * 
 * 功能说明：返回护理人员的工作量统计数据
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NurseWorkloadVO {

    /** 护理人员ID */
    private Long nurseId;

    /** 护理人员姓名 */
    private String nurseName;

    /** 护理次数 */
    private Long careCount;

    /** 服务客户数 */
    private Long customerCount;

    /** 工作量占比（百分比） */
    private Double workloadPercent;
}
