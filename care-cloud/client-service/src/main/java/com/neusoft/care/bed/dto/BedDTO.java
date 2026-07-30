package com.neusoft.care.bed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 床位请求DTO - 用于新增和修改床位接口
 * 
 * 功能说明：封装床位信息的提交数据
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedDTO {
    /** 房间号（必填） */
    @NotBlank(message = "房间号不能为空")
    private String roomNumber;
    
    /** 床号（必填） */
    @NotBlank(message = "床号不能为空")
    private String bedNumber;
    
    /** 楼层（必填） */
    @NotNull(message = "楼层不能为空")
    private Integer floor;
    
    /** 朝向（南/北/东/西等） */
    private String orientation;
    
    /** 备注 */
    private String remark;
}
