package com.neusoft.care.bed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 入住登记请求DTO - 用于入住登记接口
 * 
 * 功能说明：封装客户入住时提交的信息
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDTO {
    /** 客户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /** 床位ID（必须是空闲状态） */
    @NotNull(message = "床位ID不能为空")
    private Long bedId;
    
    /** 护理级别ID */
    @NotNull(message = "护理级别不能为空")
    private Integer careLevelId;
    
    /** 入住日期（不能晚于当前日期） */
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;
}
