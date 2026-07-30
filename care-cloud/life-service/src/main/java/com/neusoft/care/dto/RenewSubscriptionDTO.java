package com.neusoft.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 续期订阅请求DTO —— 传递到期日期或续约时长
 *
 * @author CareCenter Team
 */
@Data
@Schema(description = "续期请求")
public class RenewSubscriptionDTO {

    @Schema(description = "新的到期日期（与duration二选一）", example = "2026-12-31")
    private LocalDate newEndDate;

    @Schema(description = "续约数量（按服务计价单位：月/天/年）", example = "3")
    private Integer duration;
}
