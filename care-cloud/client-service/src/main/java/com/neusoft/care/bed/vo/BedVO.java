package com.neusoft.care.bed.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 床位信息VO - 返回给前端的床位详情
 * 
 * 功能说明：包含床位完整信息
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedVO {

    /** 床位ID */
    private Long id;

    /** 房间号 */
    private String roomNumber;

    /** 床号 */
    private String bedNumber;

    /** 楼层 */
    private Integer floor;

    /** 朝向 */
    private String orientation;

    /** 状态（0-空闲，1-占用） */
    private Integer status;

    /** 备注 */
    private String remark;
}
