package com.neusoft.care.bed.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 入住记录VO - 返回给前端的入住信息
 * 
 * 功能说明：包含入住记录详情，关联显示客户姓名、床位和护理级别
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInVO {

    /** 记录ID */
    private Long id;

    /** 客户ID */
    private Long userId;

    /** 客户姓名 */
    private String userName;

    /** 床位ID */
    private Long bedId;

    /** 房间号 */
    private String roomNumber;

    /** 床号 */
    private String bedNumber;

    /** 护理级别名称 */
    private String careLevelName;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 状态（0-入住中，1-已退住，2-外出中） */
    private Integer status;
}
