package com.neusoft.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 绑定健康管家请求DTO
 *
 * @author CareCenter Team
 */
@Data
@Schema(description = "绑定健康管家请求")
public class BindCaregiverDTO {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @NotNull(message = "管家ID不能为空")
    @Schema(description = "健康管家ID", example = "1")
    private Long adminId;
}
